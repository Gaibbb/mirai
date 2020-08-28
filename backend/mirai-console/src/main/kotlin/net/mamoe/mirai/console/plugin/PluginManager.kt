/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 license that can be found via the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")

package net.mamoe.mirai.console.plugin

import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.internal.plugin.PluginManagerImpl
import net.mamoe.mirai.console.plugin.description.PluginDescription
import net.mamoe.mirai.console.util.ConsoleExperimentalAPI
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * 插件管理器.
 *
 * [PluginManager] 管理所有 [插件加载器][PluginLoader], 储存对所有插件的引用 ([plugins]), 但不直接与 [插件实例][Plugin] 交互.
 *
 * 有关 [插件加载][PluginLoader.load], [插件启用][PluginLoader.enable] 等操作都由 [PluginLoader] 完成.
 * [PluginManager] 仅作为一个联系所有 [插件加载器][PluginLoader], 使它们互相合作的桥梁.
 *
 * 若要主动加载一个插件, 请获取相应插件的 [PluginLoader], 然后使用 [PluginLoader.enable]
 *
 * @see Plugin 插件
 * @see PluginLoader 插件加载器
 */
public interface PluginManager {
    // region paths

    /**
     * 插件自身存放路径 [Path]. 由前端决定具体路径.
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/plugins`
     */
    public val pluginsPath: Path

    /**
     * 插件自身存放路径 [File]. 由前端决定具体路径.
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/plugins`
     */
    public val pluginsFolder: File

    /**
     * 插件内部数据存放路径 [Path]
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/data`
     */
    public val pluginsDataPath: Path

    /**
     * 插件内部数据存放路径 [File]
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/data`
     */
    public val pluginsDataFolder: File

    /**
     * 插件配置存放路径 [Path]
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/config`
     */
    public val pluginsConfigPath: Path

    /**
     * 插件配置存放路径 [File]
     *
     * **实现细节**: 在 terminal 前端实现为 `$rootPath/config`
     */
    public val pluginsConfigFolder: File

    // endregion


    // region plugins & loaders

    /**
     * 已加载的插件列表
     *
     * @return 只读列表
     */
    public val plugins: List<Plugin>

    /**
     * 内建的插件加载器列表. 由 [MiraiConsole] 初始化.
     *
     * @return 只读列表
     */
    public val builtInLoaders: List<PluginLoader<*, *>>

    /**
     * 由插件创建的 [PluginLoader]
     *
     * @return 只读列表
     */
    public val pluginLoaders: List<PluginLoader<*, *>>

    /**
     * 手动注册一个扩展的插件加载器. 在启动时会通过 [ServiceLoader] 加载, 但也可以手动注册.
     *
     * @see PluginLoader 插件加载器
     */
    @ConsoleExperimentalAPI
    public fun PluginLoader<*, *>.register(): Boolean

    /**
     * 取消注册一个扩展的插件加载器
     *
     * @see PluginLoader 插件加载器
     */
    @ConsoleExperimentalAPI
    public fun PluginLoader<*, *>.unregister(): Boolean

    /**
     * 获取插件的 [描述][PluginDescription], 通过 [PluginLoader.getDescription]
     */
    public val Plugin.description: PluginDescription

    /**
     * 禁用这个插件
     *
     * @see PluginLoader.disable
     */
    public fun Plugin.disable(): Unit = safeLoader.disable(this)

    /**
     * 加载这个插件
     *
     * @see PluginLoader.load
     */
    public fun Plugin.load(): Unit = safeLoader.load(this)

    /**
     * 启用这个插件
     *
     * @see PluginLoader.enable
     */
    public fun Plugin.enable(): Unit = safeLoader.enable(this)

    /**
     * 经过泛型类型转换的 [Plugin.loader]
     */
    @get:JvmSynthetic
    @Suppress("UNCHECKED_CAST")
    public val <P : Plugin> P.safeLoader: PluginLoader<P, PluginDescription>
        get() = this.loader as PluginLoader<P, PluginDescription>

    // endregion

    public companion object INSTANCE : PluginManager by PluginManagerImpl {
        // due to Kotlin's bug
        public override val Plugin.description: PluginDescription get() = PluginManagerImpl.run { description }
        public override fun PluginLoader<*, *>.register(): Boolean = PluginManagerImpl.run { register() }
        public override fun PluginLoader<*, *>.unregister(): Boolean = PluginManagerImpl.run { unregister() }
        public override fun Plugin.disable(): Unit = PluginManagerImpl.run { disable() }
        public override fun Plugin.enable(): Unit = PluginManagerImpl.run { enable() }
        public override fun Plugin.load(): Unit = PluginManagerImpl.run { load() }
        public override val <P : Plugin> P.safeLoader: PluginLoader<P, PluginDescription> get() = PluginManagerImpl.run { safeLoader }
    }
}