/*
 * Copyright 2017-2020 The BlockyArena Contributors.
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

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.BlockyArena;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class Timer {
    private int tMinus;
    private Task task;

    /**
     * A timer that fires an action on each second.
     *
     * @param timeInterval the number of seconds
     * @param action the action to be performed on the given second
     */
    public Timer(int timeInterval, IntConsumer action) {
        tMinus = timeInterval;
        task = Task.builder()
                .interval(1, TimeUnit.SECONDS)
                .execute(() -> {
                    action.accept(tMinus--);
                    if (tMinus < 0) {
                        task.cancel();
                    }
                })
                .submit(BlockyArena.getInstance());
    }

    /**
     * Cancels the timer.
     */
    public void cancel() {
        task.cancel();
    }
}
