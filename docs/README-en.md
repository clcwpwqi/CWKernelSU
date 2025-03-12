# Sukisu

**Enlish** | [简体中文](README.md)


Android device root solution based on [KernelSU](https://github.com/KernelSU/KernelSU)

**Experimental! Use at your own risk! **This solution is based on [KernelSU]() and is experimental!

>
> This is an unofficial fork, all rights reserved [@tiann](https://github.com/tiann)
>
> This is a project based on personal use of the second change, personal research and development use only

- Fully adapted for non-GKI devices

## How to add
```
curl -LSs “https://raw.githubusercontent.com/ShirkNeko/KernelSU/main/kernel/setup.sh” | bash -s susfs-dev
```



## How to use 

Use the susfs-dev branch directly without any patching


Or use the default branch for patching, with files in [patch](../patch)

## More links
Projects compiled based on MKSU-SKN and susfs
- [GKI](https://github.com/ShirkNeko/GKI_KernelSU_SUSFS) 
- [OnePlus](https://github.com/ShirkNeko/Action_OnePlus_MKSU_SUSFS)


## Features

1. Kernel-based `su` and root access management.
2. Not based on [OverlayFS](https://en.wikipedia.org/wiki/OverlayFS) module system. 3.
3. [Application Profiles](https://kernelsu.org/guide/app-profile.html): Lock root privileges in a cage. 4.
4. more customization
5. More customizable interface and features.



## License

- The file in the “kernel” directory is [GPL-2.0-only](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html).
- All other parts except the “kernel” directory are [GPL-3.0 or later](https://www.gnu.org/licenses/gpl-3.0.html).

## Contributions

- [KernelSU](https://github.com/tiann/KernelSU): original project
- [MKSU](https://github.com/5ec1cff/KernelSU): Used project
- [RKSU](https://github.com/rsuntk/KernelsU)：Re-support of non-GKI devices using the kernel of this project
- [susfs](https://gitlab.com/simonpunk/susfs4ksu)：Used susfs file system
- [KernelSU](https://git.zx2c4.com/kernel-assisted-superuser/about/): KernelSU conceptualization
- [Magisk](https://github.com/topjohnwu/Magisk): Powerful root utility
- [genuine](https://github.com/brevent/genuine/): APK v2 Signature Verification
- [Diamorphine](https://github.com/m0nad/Diamorphine): Some rootkit skills.
