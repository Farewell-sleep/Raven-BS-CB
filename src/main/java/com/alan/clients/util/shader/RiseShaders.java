package com.alan.clients.util.shader;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.impl.AlphaShader;
import com.alan.clients.util.shader.impl.MainMenuBackgroundShader;
import com.alan.clients.util.shader.impl.OutlineShader;
import com.alan.clients.util.shader.impl.RGQShader;
import com.alan.clients.util.shader.impl.RGQTestShader;
import com.alan.clients.util.shader.impl.ROGQShader;
import com.alan.clients.util.shader.impl.ROQShader;
import com.alan.clients.util.shader.impl.BAWShader;
import com.alan.clients.util.shader.impl.BloomShader;
import com.alan.clients.util.shader.impl.GaussianBlurShader;
import com.alan.clients.util.shader.impl.RQShader;
import com.alan.clients.util.shader.impl.TriRGQShader;

public interface RiseShaders {
    AlphaShader ALPHA_SHADER = new AlphaShader();
    RiseShader aPB = new BloomShader();
    RiseShader aPC = new BloomShader();
    RiseShader aPD = new BloomShader();
    GaussianBlurShader aPE = new GaussianBlurShader();
    GaussianBlurShader aPF = new GaussianBlurShader();
    RiseShader OUTLINE_SHADER = new OutlineShader();
    RQShader RQ_SHADER = new RQShader();
    RGQShader RGQ_SHADER = new RGQShader();
    ROQShader ROQ_SHADER = new ROQShader();
    ROGQShader ROGQ_SHADER = new ROGQShader();
    RiseShader aPL = new MainMenuBackgroundShader();
    RiseShader BAW_SHADER = new BAWShader();
    RGQTestShader RGQ_TEST_SHADER = new RGQTestShader();
    TriRGQShader TRI_RGQ_SHADER = new TriRGQShader();
}
