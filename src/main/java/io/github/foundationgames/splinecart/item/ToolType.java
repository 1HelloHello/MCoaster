package io.github.foundationgames.splinecart.item;

import com.mojang.brigadier.Message;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public enum ToolType {

    HEADING(property -> {
        MutableText text = Text.translatable("item.splinecart.heading_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.GREEN);
        return text;
    }),
    PITCHING(property -> {
        MutableText text = Text.translatable("item.splinecart.pitching_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.CYAN);
        return text;
    }),
    BANKING(property -> {
        MutableText text = Text.translatable("item.splinecart.banking_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.YELLOW);
        return text;
    }),
    RELATIVE_ORIENTATION(property -> {
        MutableText text = Text.translatable("item.splinecart.relative_orientation_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.RED);
        return text;
    });

    public final MessageBuilder currentStateMsg;

    ToolType(MessageBuilder currentStateMsg) {
        this.currentStateMsg = currentStateMsg;
    }

    public interface MessageBuilder {
        Message get(Object property);
    }

}
