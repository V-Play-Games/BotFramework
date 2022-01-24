package net.vpg.bot.action;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class AbstractCRA<R extends RestAction<?>> implements CommandReplyAction {
    protected final R action;
    protected Consumer<CommandReplyAction> task;
    protected String content;

    protected AbstractCRA(@Nonnull R action) {
        this.action = action;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return action.getJDA();
    }

    @Nonnull
    @Override
    public CommandReplyAction setCheck(@Nullable BooleanSupplier checks) {
        action.setCheck(checks);
        return this;
    }

    @Nullable
    @Override
    public BooleanSupplier getCheck() {
        return action.getCheck();
    }

    @Nonnull
    @Override
    public CommandReplyAction deadline(long timestamp) {
        action.deadline(timestamp);
        return this;
    }

    @Override
    public Consumer<CommandReplyAction> getTask() {
        return task;
    }

    @Nonnull
    @Override
    public CommandReplyAction setTask(Consumer<CommandReplyAction> task) {
        this.task = task;
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction setContent(String content) {
        setContentImpl(content);
        this.content = content;
        return this;
    }

    protected abstract void setContentImpl(String content);

    @Nonnull
    @Override
    public String getContent() {
        return content;
    }
}
