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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A utility for working with {@link GoogleChatContext} identifiers.
 */
public final class Identifiers {

    /** Prevents instantiation of this utility class. **/
    private Identifiers() {
    }

    /** Creates a new {@link ThreadId} out of the specified {@code value}. **/
    public static ThreadId threadIdOf(String value) {
        checkNotNull(value);
        return ThreadId
                .newBuilder()
                .setValue(value)
                .vBuild();
    }

    /** Creates a new {@link SpaceId} out of the specified {@code value}. **/
    public static SpaceId spaceIdOf(String value) {
        checkNotNull(value);
        return SpaceId
                .newBuilder()
                .setValue(value)
                .vBuild();
    }

    /** Creates a new {@link MessageId} out of the specified {@code value}. **/
    public static MessageId messageIdOf(String value) {
        checkNotNull(value);
        return MessageId
                .newBuilder()
                .setValue(value)
                .vBuild();
    }
}
