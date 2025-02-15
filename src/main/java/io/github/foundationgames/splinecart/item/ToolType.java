package io.github.foundationgames.splinecart.item;

import com.mojang.brigadier.Message;
import io.github.foundationgames.splinecart.block.TrackTiesBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public enum ToolType {

    HEADING(TrackTiesBlock.HEADING, property -> {
        MutableText text = Text.translatable("item.splinecart.heading_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.GREEN);
        return text;
    }),
    PITCHING(TrackTiesBlock.PITCHING, property -> {
        MutableText text = Text.translatable("item.splinecart.pitching_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.CYAN);
        return text;
    }),
    BANKING(TrackTiesBlock.BANKING, property -> {
        MutableText text = Text.translatable("item.splinecart.banking_tool.msg").append(Text.of(property.toString()));
        text.withColor(Colors.YELLOW);
        return text;
    });

    public final Property<?> property;
    public final MessageBuilder currentStateMsg;

    ToolType(Property<?> property, MessageBuilder currentStateMsg) {
        this.property = property;
        this.currentStateMsg = currentStateMsg;
    }

    public interface MessageBuilder {
        Message get(Object property);
    }

}
