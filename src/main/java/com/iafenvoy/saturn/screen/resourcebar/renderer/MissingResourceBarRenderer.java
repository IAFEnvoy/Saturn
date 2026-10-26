package com.iafenvoy.saturn.screen.resourcebar.renderer;

import com.iafenvoy.saturn.data.resourcebar.builtin.renderdata.MissingRenderData;
import com.iafenvoy.saturn.screen.resourcebar.ResourceBarRenderer;

public final class MissingResourceBarRenderer extends ResourceBarRenderer<MissingRenderData> {

    @Override
    public void render(MissingRenderData data, Context context) {
        ResourceBarRendererHelper.decorations(context, false);
    }
}
