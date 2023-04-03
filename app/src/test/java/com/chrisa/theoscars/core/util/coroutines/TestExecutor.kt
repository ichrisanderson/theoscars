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

package com.chrisa.theoscars.core.util.coroutines

import java.util.LinkedList
import java.util.concurrent.Executor

class TestExecutor : Executor {
    /**
     * If true, adding a new task will drain all existing tasks.
     */
    var autoRun: Boolean = true

    private val mTasks = LinkedList<Runnable>()

    override fun execute(command: Runnable) {
        mTasks.add(command)
        if (autoRun) {
            executeAll()
        }
    }

    fun executeAll(): Boolean {
        val consumed = !mTasks.isEmpty()

        var task = mTasks.poll()
        while (task != null) {
            task.run()
            task = mTasks.poll()
        }
        return consumed
    }
}
