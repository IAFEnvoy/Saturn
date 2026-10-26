package com.iafenvoy.saturn.screen.resourcebar.renderer;

import com.iafenvoy.saturn.data.resourcebar.builtin.renderdata.SegmentedRenderData;
import com.iafenvoy.saturn.screen.resourcebar.ResourceBarRenderer;

public final class SegmentedResourceBarRenderer extends ResourceBarRenderer<SegmentedRenderData> {

    @Override
    public void render(SegmentedRenderData data, Context context) {
        int filled = (int) Math.round(context.state().percentage() * data.segments());
        for (int index = 0; index < data.segments(); index++) {
            int x = context.x() + index * (8 + data.gap());
            context.graphics().fill(x, context.y(), x + 8, context.y() + 8,
                    0xFF000000 | (index < filled ? data.fullColor() : data.emptyColor()));
        }
        ResourceBarRendererHelper.decorations(context, true);
    }
}
