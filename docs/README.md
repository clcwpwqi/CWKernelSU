# CWKernelSU

**简体中文** | [English](README-en.md)

基于 [KernelSU](https://github.com/tiann/KernelSU) 的安卓设备 root 解决方案

**实验性!使用风险自负!**


>
> 这是非官方分叉，保留所有权利 [@tiann](https://github.com/tiann)
>
> 这是个基于个人使用二改的项目，仅限个人研究开发使用


## 如何添加
在内核源码的根目录下执行此命令

```
curl -LSs "https://raw.githubusercontent.com/clcwpwqi/CWKernelSU/main/kernel/setup.sh" | bash -s susfs-dev
```

## 如何使用 

直接使用 susfs-dev 分支，不需要打任何补丁


或者对默认分支进行补丁，补丁文件在 [patch](../patch) 目录里


## 特点

1. 基于内核的 `su` 和 root 访问管理。
2. 非基于 [OverlayFS](https://en.wikipedia.org/wiki/OverlayFS) 的模块系统。
3. [App Profile](https://kernelsu.org/guide/app-profile.html)： 将 root 权限锁在笼子里。
4. 更多自定义功能
5. 更适合使用习惯的界面和功能
6. 完全适配非 GKI 设备


## 许可证

- " kernel "目录下的文件是[GPL-2.0-only](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)。
- 除 “kernel ”目录外，所有其他部分均为[GPL-3.0 或更高版本](https://www.gnu.org/licenses/gpl-3.0.html)。

## 贡献

- [KernelSU](https://github.com/tiann/KernelSU)： 原始项目
- [MKSU](https://github.com/5ec1cff/KernelSU)：使用的项目
- [RKSU](https://github.com/rsuntk/KernelsU):使用该项目的kernel对非GKI设备进行重新支持
- [SukiSU](https://github.com/ShirkNeko/KernelSU?tab=readme-ov-file):本项目的基础，在该项目上进行的基于个人喜好的轻微修改

- [susfs](https://gitlab.com/simonpunk/susfs4ksu)：使用的susfs文件系统
- [kernel-assisted-superuser](https://git.zx2c4.com/kernel-assisted-superuser/about/)： KernelSU 的构想
- [Magisk](https://github.com/topjohnwu/Magisk)： 强大的 root 工具
- [genuine](https://github.com/brevent/genuine/)： APK v2 签名验证
- [Diamorphine](https://github.com/m0nad/Diamorphine)： 一些 rootkit 技能
