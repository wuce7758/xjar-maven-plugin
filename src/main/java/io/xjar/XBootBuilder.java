package io.xjar;

import io.xjar.boot.XBoot;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * XJar - Spring-Boot JAR 加密插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 14:02
 */
@Mojo(name = "spring-boot", defaultPhase = LifecyclePhase.PACKAGE)
public class XBootBuilder extends XBuilder {

    @Override
    protected void build(File src, File dest, String password, String algorithm, int keySize, int ivSize, XEntryFilter<JarArchiveEntry> filter) throws Exception {
        XBoot.encrypt(src, dest, password, algorithm, keySize, ivSize, filter);
    }

}
