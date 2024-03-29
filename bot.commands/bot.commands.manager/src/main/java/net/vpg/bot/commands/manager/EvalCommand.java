/*
 * Copyright 2021 Vaibhav Nargwani
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
package net.vpg.bot.commands.manager;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.Check;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;

import javax.script.*;
import java.util.stream.Collectors;

public class EvalCommand extends BotCommandImpl {
    public static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("groovy");
    private static final String PACKAGES = new ClassGraph()
        .enableClassInfo()
        .scan()
        .getAllClasses()
        .stream()
        .map(ClassInfo::getPackageName)
        .filter(name -> name.startsWith("net.dv8tion.jda"))
        .distinct()
        .map(p -> "import " + p + ";")
        .collect(Collectors.joining("\n"));

    public EvalCommand(Bot bot) {
        super(bot, "eval", "Evaluates an expression, or a piece of code");
        addOption(OptionType.STRING, "script", "The expression or code to be evaluated", true);
        addCheck(Check.requiresManager(getBot()));
    }

    @Override
    public void execute(CommandReceivedEvent e) throws ScriptException {
        String script = e.getString("script");
        long startTime = System.currentTimeMillis();
        Bindings bindings = new SimpleBindings();
        bindings.put("e", e);
        bindings.put("event", e);
        bindings.put("guild", e.getGuild());
        bindings.put("channel", e.getChannel());
        bindings.put("author", e.getUser());
        bindings.put("bot", e.getBot());
        bindings.put("jda", e.getJDA());
        bindings.put("script", script);
        bindings.put("startTime", startTime);
        Object result = ENGINE.eval(script, bindings);
        e.reply("Successfully Executed in " + (System.currentTimeMillis() - startTime) + " ms" + (result == null ? "" : "\nResult:`" + result.toString()) + "`").queue();
    }
}
