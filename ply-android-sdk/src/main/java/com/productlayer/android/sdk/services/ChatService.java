/*
 * Copyright (c) 2016, ProductLayer GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.chat.ChatGroup;
import com.productlayer.core.beans.chat.ChatMessage;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

public class ChatService {

    /**
     * Add users to the chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param userIds
     *         The userIds
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated chat group.
     * @return a Future object to optionally wait for the {@code ChatGroup} result or to cancel the query
     */
    public static Future<ChatGroup> addUserToChatGroup(final PLYAndroid client, final String chatGroupId,
            final List<String> userIds, PLYCompletion<ChatGroup> completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup>() {
            @Override
            public ChatGroup execute() {
                return com.productlayer.rest.client.services.ChatService.addUserToChatGroup(client
                        .getRestClient(), chatGroupId, userIds);
            }
        }, completion);
    }

    /**
     * Get a specific chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param body
     *         The body
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated chat group.
     * @return a Future object to optionally wait for the {@code ChatGroup} result or to cancel the query
     */
    public static Future<ChatGroup> changeChatGroupTitle(final PLYAndroid client, final String chatGroupId,
            final HashMap<String, String> body, PLYCompletion<ChatGroup> completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup>() {
            @Override
            public ChatGroup execute() {
                return com.productlayer.rest.client.services.ChatService.changeChatGroupTitle(client
                        .getRestClient(), chatGroupId, body);
            }
        }, completion);
    }

    /**
     * Create a new chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param group
     *         The group
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         created chat group.
     * @return a Future object to optionally wait for the {@code ChatGroup} result or to cancel the query
     */
    public static Future<ChatGroup> createChatGroups(final PLYAndroid client, final ChatGroup group,
            PLYCompletion<ChatGroup> completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup>() {
            @Override
            public ChatGroup execute() {
                return com.productlayer.rest.client.services.ChatService.createChatGroups(client
                        .getRestClient(), group);
            }
        }, completion);
    }

    /**
     * Get the chat groups for the logged in user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: A list of
     *         chat groups for the logged in user.
     * @return a Future object to optionally wait for the {@code ChatGroup[]} result or to cancel the query
     */
    public static Future<ChatGroup[]> getChatGroups(final PLYAndroid client, PLYCompletion<ChatGroup[]>
            completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup[]>() {
            @Override
            public ChatGroup[] execute() {
                return com.productlayer.rest.client.services.ChatService.getChatGroups(client.getRestClient
                        ());
            }
        }, completion);
    }

    /**
     * Get chat messages from the chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param sinceTimestamp
     *         [Optional] Request entries since this timestamp in ms since 01.01.1970.
     * @param untilTimestamp
     *         [Optional] Request entries until this timestamp in ms since 01.01.1970.
     * @param count
     *         [Optional] The amount of results to be returned, default and maximum: '200'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The chat
     *         messages.
     * @return a Future object to optionally wait for the {@code ChatMessage[]} result or to cancel the query
     */
    public static Future<ChatMessage[]> getChatMessagesFromGroup(final PLYAndroid client, final String
            chatGroupId, final Long sinceTimestamp, final Long untilTimestamp, final Integer count,
            PLYCompletion<ChatMessage[]> completion) {
        return client.submit(new PLYAndroid.Query<ChatMessage[]>() {
            @Override
            public ChatMessage[] execute() {
                return com.productlayer.rest.client.services.ChatService.getChatMessagesFromGroup(client
                        .getRestClient(), chatGroupId, sinceTimestamp, untilTimestamp, count);
            }
        }, completion);
    }

    /**
     * Get a specific chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The chat
     *         group for the specified id.
     * @return a Future object to optionally wait for the {@code ChatGroup} result or to cancel the query
     */
    public static Future<ChatGroup> getSpecificChatGroup(final PLYAndroid client, final String chatGroupId,
            PLYCompletion<ChatGroup> completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup>() {
            @Override
            public ChatGroup execute() {
                return com.productlayer.rest.client.services.ChatService.getSpecificChatGroup(client
                        .getRestClient(), chatGroupId);
            }
        }, completion);
    }

    /**
     * Send a new message.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param message
     *         The message
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The chat
     *         message.
     * @return a Future object to optionally wait for the {@code ChatMessage} result or to cancel the query
     */
    public static Future<ChatMessage> postChatMessage(final PLYAndroid client, final String chatGroupId,
            final ChatMessage message, PLYCompletion<ChatMessage> completion) {
        return client.submit(new PLYAndroid.Query<ChatMessage>() {
            @Override
            public ChatMessage execute() {
                return com.productlayer.rest.client.services.ChatService.postChatMessage(client
                        .getRestClient(), chatGroupId, message);
            }
        }, completion);
    }

    /**
     * Remove user from the chat group.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param chatGroupId
     *         The identifier of the chat group.
     * @param userId
     *         The identifier of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated chat group.
     * @return a Future object to optionally wait for the {@code ChatGroup} result or to cancel the query
     */
    public static Future<ChatGroup> removeUserFromChatGroup(final PLYAndroid client, final String
            chatGroupId, final String userId, PLYCompletion<ChatGroup> completion) {
        return client.submit(new PLYAndroid.Query<ChatGroup>() {
            @Override
            public ChatGroup execute() {
                return com.productlayer.rest.client.services.ChatService.removeUserFromChatGroup(client
                        .getRestClient(), chatGroupId, userId);
            }
        }, completion);
    }
}
