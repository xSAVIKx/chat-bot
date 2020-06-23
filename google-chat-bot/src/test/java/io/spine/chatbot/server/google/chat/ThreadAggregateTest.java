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

import io.spine.chatbot.google.chat.MessageId;
import io.spine.chatbot.google.chat.SpaceId;
import io.spine.chatbot.google.chat.ThreadId;
import io.spine.chatbot.google.chat.event.MessageCreated;
import io.spine.chatbot.google.chat.event.ThreadCreated;
import io.spine.chatbot.google.chat.thread.Thread;
import io.spine.chatbot.google.chat.thread.ThreadResource;
import io.spine.chatbot.google.chat.thread.event.MessageAdded;
import io.spine.chatbot.google.chat.thread.event.ThreadInitialized;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.chatbot.server.google.chat.GoogleChatIdentifier.message;
import static io.spine.chatbot.server.google.chat.GoogleChatIdentifier.space;
import static io.spine.chatbot.server.google.chat.GoogleChatIdentifier.thread;
import static io.spine.chatbot.server.google.chat.ThreadResources.threadResourceOf;

@DisplayName("ThreadAggregate should")
final class ThreadAggregateTest extends GoogleChatContextAwareTest {

    @Nested
    @DisplayName("initialize a thread")
    final class InitThread {

        private final ThreadId threadId = thread("SpineEventEngine/base");
        private final SpaceId spaceId = space("spaces/qpojdwpiq1241");
        private final ThreadResource threadResource =
                threadResourceOf("spaces/qpojdwpiq1241/threads/qwdojp12");

        @BeforeEach
        void setUp() {
            var threadCreated = ThreadCreated
                    .newBuilder()
                    .setId(threadId)
                    .setSpaceId(spaceId)
                    .setThread(threadResource)
                    .vBuild();
            context().receivesEvent(threadCreated);
        }

        @Test
        @DisplayName("producing ThreadInitialized event")
        void producingEvent() {
            var threadInitialized = ThreadInitialized
                    .newBuilder()
                    .setId(threadId)
                    .setSpaceId(spaceId)
                    .setThread(threadResource)
                    .vBuild();
            context().assertEvent(threadInitialized);
        }

        @Test
        @DisplayName("setting aggregate state")
        void settingState() {
            var state = Thread
                    .newBuilder()
                    .setId(threadId)
                    .setSpaceId(spaceId)
                    .setThread(threadResource)
                    .vBuild();
            context().assertState(threadId, Thread.class)
                     .isEqualTo(state);
        }
    }

    @Nested
    @DisplayName("add created message")
    final class AddMessage {

        private final ThreadId threadId = thread("SpineEventEngine/base");
        private final SpaceId spaceId = space("spaces/qpojdwpiq1241");
        private final MessageId messageId =
                message("spaces/qpojdwpiq1241/messages/dqpwjpop12");
        private final ThreadResource threadResource =
                threadResourceOf("spaces/qpojdwpiq1241/threads/qwdojp12");

        @BeforeEach
        void setUp() {
            var threadCreated = ThreadCreated
                    .newBuilder()
                    .setId(threadId)
                    .setSpaceId(spaceId)
                    .setThread(threadResource)
                    .vBuild();
            var messageCreated = MessageCreated
                    .newBuilder()
                    .setId(messageId)
                    .setSpaceId(spaceId)
                    .setThreadId(threadId)
                    .vBuild();
            context().receivesEvent(threadCreated)
                     .receivesEvent(messageCreated);
        }

        @Test
        @DisplayName("producing MessageAdded event")
        void producingEvent() {
            var messageAdded = MessageAdded
                    .newBuilder()
                    .setId(messageId)
                    .setThreadId(threadId)
                    .vBuild();
            context().assertEvent(messageAdded);
        }

        @Test
        @DisplayName("setting aggregate state")
        void settingState() {
            var state = Thread
                    .newBuilder()
                    .setId(threadId)
                    .setSpaceId(spaceId)
                    .setThread(threadResource)
                    .addMessages(messageId)
                    .vBuild();
            context().assertState(threadId, Thread.class)
                     .isEqualTo(state);
        }
    }
}
