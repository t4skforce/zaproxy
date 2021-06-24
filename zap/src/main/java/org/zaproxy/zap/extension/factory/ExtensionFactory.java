package org.zaproxy.zap.extension.factory;

import java.io.File;

import org.parosproxy.paros.extension.Extension;

public interface ExtensionFactory {

    void loadAll();

    void loadFolders(File... folders);

    void unload(Extension extension);

}
