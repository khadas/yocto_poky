LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.0-only & LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674"

SRC_URI = "${GNU_MIRROR}/gdb/gdb-${PV}.tar.xz \
           file://0001-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0002-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0003-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0004-use-asm-sgidefs.h.patch \
           file://0005-Change-order-of-CFLAGS.patch \
           file://0006-resolve-restrict-keyword-conflict.patch \
           file://0007-Fix-invalid-sigprocmask-call.patch \
           file://0008-Define-alignof-using-_Alignof-when-using-C11-or-newe.patch \
           file://0009-gdbserver-linux-low.cc-Fix-a-typo-in-ternary-operato.patch \
           file://add-missing-ldflags.patch \
           file://0001-aarch64-Check-for-valid-inferior-thread-regcache-bef.patch \
           "
SRC_URI[sha256sum] = "115ad5c18d69a6be2ab15882d365dda2a2211c14f480b3502c6eba576e2e95a0"

TOOLCHAIN = "gcc"