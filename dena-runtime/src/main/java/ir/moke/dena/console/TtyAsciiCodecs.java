/*
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

package ir.moke.dena.console;

public interface TtyAsciiCodecs {
    char ESC = (char) 27;
    String RESET = ESC + "[0m";
    String BOLD = ESC + "[1m";
    String BLINK = ESC + "[5m";

    String RED = ESC + "[31m";
    String GREEN = ESC + "[32m";
    String YELLOW = ESC + "[33m";
    String BLUE = ESC + "[34m";
    String PURPLE = ESC + "[35m";

    String BACKGROUND_RED = ESC + "[41m";
    String BACKGROUND_GREEN = ESC + "[42m";
    String BACKGROUND_YELLOW = ESC + "[43m";
    String BACKGROUND_BLUE = ESC + "[44m";
}
