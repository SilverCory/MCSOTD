/*
 * http://ryred.co/
 * ace[at]ac3-servers.eu
 *
 * =================================================================
 *
 * Copyright (c) 2016, Cory Redmond
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of MCSOTD nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package co.ryred.mcsotd;

import com.drdanick.McRKit.ToolkitEvent;
import com.drdanick.McRKit.module.Module;
import com.drdanick.McRKit.module.ModuleLoader;
import com.drdanick.McRKit.module.ModuleMetadata;

/**
 * Created by Cory Redmond on 14/03/2016.
 *
 * @author Cory Redmond <ace@ac3-servers.eu>
 */
public class RTKModule extends Module {

    private MCSOTD mcsotd;

    public RTKModule(ModuleMetadata moduleMetadata, ModuleLoader moduleLoader, ClassLoader classLoader) {
        super(moduleMetadata, moduleLoader, classLoader, ToolkitEvent.ON_SERVER_HOLD, ToolkitEvent.ON_SERVER_RESTART);
    }

    @Override
    public void onEnable() {

        this.mcsotd = new MCSOTD();
        this.mcsotd.start();

    }

    @Override
    public void onDisable() {

        this.mcsotd.stop();

    }
}
