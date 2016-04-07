# linux-yocto-custom.bb:
#
#   An example kernel recipe that uses the linux-yocto and oe-core
#   kernel classes to apply a subset of yocto kernel management to git
#   managed kernel repositories.
#
#   To use linux-yocto-custom in your layer, create a
#   linux-yocto-custom.bbappend file containing at least the following
#   lines:
#
#     FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
#     COMPATIBLE_MACHINE_yourmachine = "yourmachine"
#
#   You must also provide a Linux kernel configuration. The most direct
#   method is to copy your .config to files/defconfig in your layer,
#   in the same directory as the bbappend and add file://defconfig to
#   your SRC_URI.
#
#   To use the yocto kernel tooling to generate a BSP configuration
#   using modular configuration fragments, see the yocto-bsp and
#   yocto-kernel tools documentation.
#
# Warning:
#
#   Building this example without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition (for kernel v3.4 only):
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition (for kernel v3.4 only):
#            SRC_URI += "file://feature.scc"
#

inherit kernel
require recipes-kernel/linux/linux-yocto.inc
FILESEXTRAPATHS_prepend:= "${THISDIR}/${PN}_${PV}:"

LINUX_VERSION ?= "3.10"

SRC_URI = " \
	git://github.com/hardkernel/linux.git;protocol=git;nocheckout=1;branch=${SRCBRANCH} \
	file://0001-MALI-upgrade-to-TX011-SW-99002-r5p0-06rel0.patch \
	file://0002-MALI-platform-5422-fixed-for-r5p0-06rel0.patch \
	file://0003-ARM-exynos5422_evt0.dtsi-added-IRQ-description.patch \
	file://0004-MALI-Build-kernel-driver-as-a-module.patch \
	file://0005-ARM-odroidxu3-enable-CONFIG_PROFILING.patch \
	file://0006-ARM-odroidxu3_defconfig-enable-Mali-Gator-support.patch \
	file://0007-MALI-add-gpuinfo-sysfs-entry.patch \
	file://0008-ARM-drm-exynos-re-enable-support-for-triple-bufferin.patch \
	file://0009-ARM-drm-exynos-take-a-reference-to-the-DMA-buf.patch \
	file://0010-DRM-EXYNOS-Add-dma_buf-mmap-support.patch \
	file://0011-CHROMIUM-drm-exynos-dmabuf-Use-dma_mmap_attrs-to-mma.patch \
	file://0012-drivers-gpu-arm-midgard-update-to-r6p0-02rel0.patch \
	file://0013-drivers-gpu-arm-midgard-fix-5422-platform-files-for-.patch \
	file://0014-drm-exynos-Clear-vm_pgoff-before-forwarding-mmap.patch \
	file://0015-ARM-DMA-Use-vm_pgoff-for-IOMMU-mmap.patch \
	file://0016-drm-exynos-fix-formating-in-some-functions.patch \
	file://0017-fbdev-add-dma-buf-support.patch \
	file://0018-drm-exynos-switch-to-using-standard-fb-ioctl.patch \
	file://0019-EXPERIMENTAL-drm-exynos-enable-Vsync-for-dma-fbdev.patch \
	file://0020-drm-exynos-fix-fbdev-triple-buffering-support.patch \
	file://0021-kernel-use-the-gnu89-standard-explicitly.patch \
"

SRCBRANCH = "odroidxu3-3.10.y"
SRCREV = "36898ffe2082ad378f2207555eee0ad22e21381c"
SRCREV_machine = "${SRCREV}"

PR = "69"
PV = "${LINUX_VERSION}"

COMPATIBLE_MACHINE = "(odroidxu3)"

do_kernel_configme() {
	oe_runmake -C ${S} O=${B} allnoconfig
}

do_configure_prepend() {
	oe_runmake odroidxu3_defconfig
	oe_runmake oldconfig
}

do_kernel_checkout_append() {
	git branch master
}
