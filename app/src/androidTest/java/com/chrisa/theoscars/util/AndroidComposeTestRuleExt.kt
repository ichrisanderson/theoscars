/*
 * Copyright 2023 Chris Anderson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisa.theoscars.util

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onAllNodesWithStringResId(
    @StringRes id: Int,
): SemanticsNodeInteractionCollection = onAllNodesWithText(activity.getString(id))

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringResId(
    @StringRes id: Int,
): SemanticsNodeInteraction = onNodeWithText(activity.getString(id))

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.getString(
    @StringRes id: Int,
    vararg args: Any,
): String {
    return activity.getString(id, *args)
}

private const val defaultTimeoutMillis = 5000L

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitOnAllNodesWithText(
    text: String,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodesWithText(text = text, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitOnAllNodesWithTag(
    tag: String,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodesWithTag(testTag = tag, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitOnAllNodesWithStringResId(
    @StringRes id: Int,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodesWithText(text = activity.getString(id), useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitOnAllNodesWithContentDescription(
    contentDescription: String,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodesWithContentDescription(label = contentDescription, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.assertNodeWithStringResIdDoesNotExist(
    @StringRes id: Int,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodesWithText(text = activity.getString(id), useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isEmpty()
    }

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.waitOnAllNodesWithMatcher(
    matcher: SemanticsMatcher,
    useUnmergedTree: Boolean = false,
    timeoutMillis: Long = defaultTimeoutMillis,
) =
    this.waitUntil(timeoutMillis = timeoutMillis) {
        this.onAllNodes(matcher = matcher, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }
