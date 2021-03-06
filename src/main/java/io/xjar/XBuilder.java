package io.xjar;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * XJar 构建插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 10:27
 */
public abstract class XBuilder extends AbstractMojo {

    /**
     * 加密算法名称
     */
    @Parameter(property = "xjar.algorithm", required = true, defaultValue = "AES")
    protected String algorithm;

    /**
     * 加密密钥长度
     */
    @Parameter(property = "xjar.keySize", required = true, defaultValue = "128")
    protected int keySize;

    /**
     * 加密向量长度
     */
    @Parameter(property = "xjar.ivSize", required = true, defaultValue = "128")
    protected int ivSize;

    /**
     * 加密密码
     */
    @Parameter(property = "xjar.password", required = true)
    protected String password;

    /**
     * 原本JAR所在文件夹
     */
    @Parameter(property = "xjar.sourceDir", required = true, defaultValue = "${project.build.directory}")
    protected File sourceDir;

    /**
     * 原本JAR名称
     */
    @Parameter(property = "xjar.sourceJar", required = true, defaultValue = "${project.build.finalName}.jar")
    protected String sourceJar;

    /**
     * 生成JAR所在文件夹
     */
    @Parameter(property = "xjar.targetDir", required = true, defaultValue = "${project.build.directory}")
    protected File targetDir;

    /**
     * 生成JAR名称
     */
    @Parameter(property = "xjar.targetJar", required = true, defaultValue = "${project.build.finalName}.xjar")
    protected String targetJar;

    /**
     * 包含资源，避免和excludes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * BOOT-INF/classes/**
     * BOOT-INF/lib/*.jar
     */
    @Parameter(property = "xjar.includes")
    protected String[] includes;

    /**
     * 排除资源，避免和includes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * BOOT-INF/classes/**
     * BOOT-INF/lib/*.jar
     */
    @Parameter(property = "xjar.excludes")
    protected String[] excludes;

    /**
     * 项目打包模式，只对packaging == jar 的模块构建XJar
     */
    @Parameter(readonly = true, defaultValue = "${project.packaging}")
    protected String packaging;

    public void execute() throws MojoExecutionException {
        Log log = getLog();
        if (!"jar".equalsIgnoreCase(packaging)) {
            log.info("Skip for packaging: " + packaging);
            return;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Using algorithm: " + algorithm);
                log.debug("Using key-size: " + keySize);
                log.debug("Using iv-size: " + ivSize);
                log.debug("Using password: " + algorithm);
            }
            File src = new File(sourceDir, sourceJar);
            File dest = new File(targetDir, targetJar);
            File folder = dest.getParentFile();
            if (!folder.exists() && !folder.mkdirs() && !folder.exists()) {
                throw new IOException("could not make directory: " + folder);
            }
            log.info("Building xjar: " + dest + " for jar: " + src);
            XEntryFilter<JarArchiveEntry> filter;
            if (includes != null && includes.length > 0) {
                XAnyEntryFilter<JarArchiveEntry> xIncludesFilter = XEntryFilters.any();
                for (int i = 0; includes != null && i < includes.length; i++) {
                    xIncludesFilter.mix(new XIncludeAntEntryFilter(includes[i]));
                    log.info("Including " + includes[i]);
                }
                filter = xIncludesFilter;
            } else if (excludes != null && excludes.length > 0) {
                XAllEntryFilter<JarArchiveEntry> xExcludesFilter = XEntryFilters.all();
                for (int i = 0; excludes != null && i < excludes.length; i++) {
                    xExcludesFilter.mix(new XExcludeAntEntryFilter(excludes[i]));
                    log.info("Excluding " + excludes[i]);
                }
                filter = xExcludesFilter;
            } else {
                filter = null;
                log.info("Including all resources");
            }
            build(src, dest, password, algorithm, keySize, ivSize, filter);
        } catch (Exception e) {
            throw new MojoExecutionException("could not build xjar", e);
        }
    }

    /**
     * 构建XJar包
     *
     * @param src       原文包
     * @param dest      加密包
     * @param password  密码
     * @param algorithm 加密算法
     * @param keySize   密钥长度
     * @param ivSize    向量长度
     * @param filter    过滤器
     * @throws Exception 加密异常
     */
    protected abstract void build(File src, File dest, String password, String algorithm, int keySize, int ivSize, XEntryFilter<JarArchiveEntry> filter) throws Exception;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public int getIvSize() {
        return ivSize;
    }

    public void setIvSize(int ivSize) {
        this.ivSize = ivSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getSourceJar() {
        return sourceJar;
    }

    public void setSourceJar(String sourceJar) {
        this.sourceJar = sourceJar;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    public String getTargetJar() {
        return targetJar;
    }

    public void setTargetJar(String targetJar) {
        this.targetJar = targetJar;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }
}
