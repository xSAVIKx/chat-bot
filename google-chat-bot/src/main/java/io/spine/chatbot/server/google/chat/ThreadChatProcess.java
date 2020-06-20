/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.chatbot.server.google.chat;

import com.google.errorprone.annotations.concurrent.LazyInit;
import io.spine.chatbot.api.GoogleChatClient;
import io.spine.chatbot.github.RepositoryId;
import io.spine.chatbot.github.repository.build.BuildState;
import io.spine.chatbot.github.repository.build.event.BuildFailed;
import io.spine.chatbot.github.repository.build.event.BuildRecovered;
import io.spine.chatbot.google.chat.ThreadId;
import io.spine.chatbot.google.chat.event.MessageCreated;
import io.spine.chatbot.google.chat.event.ThreadCreated;
import io.spine.chatbot.google.chat.thread.ThreadChat;
import io.spine.core.External;
import io.spine.logging.Logging;
import io.spine.protobuf.Messages;
import io.spine.server.event.React;
import io.spine.server.procman.ProcessManager;
import io.spine.server.tuple.Pair;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Optional;

import static io.spine.chatbot.server.google.chat.Identifiers.messageIdOf;
import static io.spine.chatbot.server.google.chat.Identifiers.spaceIdOf;
import static io.spine.chatbot.server.google.chat.Identifiers.threadIdOf;
import static io.spine.chatbot.server.google.chat.ThreadResources.threadResourceOf;

/**
 * A process of notifying thread members about the changes in the watched resouces.
 */
final class ThreadChatProcess extends ProcessManager<ThreadId, ThreadChat, ThreadChat.Builder>
        implements Logging {

    @LazyInit
    private @MonotonicNonNull GoogleChatClient googleChatClient;

    /**
     * Notifies thread members about a failed CI build.
     */
    @React
    Pair<MessageCreated, Optional<ThreadCreated>> on(@External BuildFailed e) {
        var change = e.getChange();
        var buildState = change.getNewValue();
        var repositoryId = e.getId();
        _info().log("Build for repository `%s` failed.", repositoryId.getValue());

        return processBuildStateUpdate(buildState, repositoryId);
    }

    /**
     * Notifies thread members about a recovered CI build.
     *
     * <p>The build is considered a recovered when it changes its state from
     * {@code failed} to {@code passing}.
     */
    @React
    Pair<MessageCreated, Optional<ThreadCreated>> on(@External BuildRecovered e) {
        var change = e.getChange();
        var buildState = change.getNewValue();
        var repositoryId = e.getId();
        _info().log("Build for repository `%s` recovered.", repositoryId.getValue());

        return processBuildStateUpdate(buildState, repositoryId);
    }

    private Pair<MessageCreated, Optional<ThreadCreated>>
    processBuildStateUpdate(BuildState buildState, RepositoryId repositoryId) {
        var sentMessage = googleChatClient.sendBuildStateUpdate(buildState, state().getThread());
        var messageId = messageIdOf(sentMessage.getName());
        var threadId = threadIdOf(repositoryId.getValue());
        var spaceId = spaceIdOf(buildState.getGoogleChatSpace());
        var messageCreated = MessageCreated
                .newBuilder()
                .setId(messageId)
                .setSpaceId(spaceId)
                .setThreadId(threadId)
                .vBuild();
        if (shouldCreateThread()) {
            var newThread = threadResourceOf(sentMessage.getThread()
                                                        .getName());
            _debug().log("New thread `%s` created for repository `%s`.",
                         newThread.getName(), repositoryId.getValue());
            builder().setThread(newThread)
                     .setSpaceId(spaceId);
            var threadCreated = ThreadCreated
                    .newBuilder()
                    .setId(threadId)
                    .setThread(newThread)
                    .setSpaceId(spaceId)
                    .vBuild();
            return Pair.withNullable(messageCreated, threadCreated);
        }
        return Pair.withNullable(messageCreated, null);
    }

    private boolean shouldCreateThread() {
        return Messages.isDefault(state().getThread());
    }

    void setGoogleChatClient(GoogleChatClient googleChatClient) {
        this.googleChatClient = googleChatClient;
    }
}
