SUMMARY = "Command line tool and library for client-side URL transfers"
DESCRIPTION = "It uses URL syntax to transfer data to and from servers. \
curl is a widely used because of its ability to be flexible and complete \
complex tasks. For example, you can use curl for things like user authentication, \
HTTP post, SSL connections, proxy support, FTP uploads, and more!"
HOMEPAGE = "https://curl.se/"
BUGTRACKER = "https://github.com/curl/curl/issues"
SECTION = "console/network"
LICENSE = "curl"
LIC_FILES_CHKSUM = "file://COPYING;md5=db8448a1e43eb2125f7740fc397db1f6"

SRC_URI = " \
    https://curl.se/download/${BP}.tar.xz \
    file://run-ptest \
    file://disable-tests \
"
SRC_URI[sha256sum] = "42ab8db9e20d8290a3b633e7fbb3cec15db34df65fd1015ef8ac1e4723750eeb"

# Curl has used many names over the years...
CVE_PRODUCT = "haxx:curl haxx:libcurl curl:curl curl:libcurl libcurl:libcurl daniel_stenberg:curl"

inherit autotools pkgconfig binconfig multilib_header ptest

# Entropy source for random PACKAGECONFIG option
RANDOM ?= "/dev/urandom"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} aws basic-auth bearer-auth digest-auth negotiate-auth libidn openssl proxy random threaded-resolver verbose zlib"
PACKAGECONFIG:class-native = "ipv6 openssl proxy random threaded-resolver verbose zlib"
PACKAGECONFIG:class-nativesdk = "ipv6 openssl proxy random threaded-resolver verbose zlib"

# 'ares' and 'threaded-resolver' are mutually exclusive
PACKAGECONFIG[ares] = "--enable-ares,--disable-ares,c-ares,,,threaded-resolver"
PACKAGECONFIG[aws] = "--enable-aws,--disable-aws"
PACKAGECONFIG[basic-auth] = "--enable-basic-auth,--disable-basic-auth"
PACKAGECONFIG[bearer-auth] = "--enable-bearer-auth,--disable-bearer-auth"
PACKAGECONFIG[brotli] = "--with-brotli,--without-brotli,brotli"
PACKAGECONFIG[builtinmanual] = "--enable-manual,--disable-manual"
# Don't use this in production
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[digest-auth] = "--enable-digest-auth,--disable-digest-auth"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[gopher] = "--enable-gopher,--disable-gopher,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[kerberos-auth] = "--enable-kerberos-auth,--disable-kerberos-auth"
PACKAGECONFIG[krb5] = "--with-gssapi,--without-gssapi,krb5"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,openldap"
PACKAGECONFIG[libgsasl] = "--with-libgsasl,--without-libgsasl,libgsasl"
PACKAGECONFIG[libidn] = "--with-libidn2,--without-libidn2,libidn2"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"
PACKAGECONFIG[mbedtls] = "--with-mbedtls=${STAGING_DIR_TARGET},--without-mbedtls,mbedtls"
PACKAGECONFIG[mqtt] = "--enable-mqtt,--disable-mqtt,"
PACKAGECONFIG[negotiate-auth] = "--enable-negotiate-auth,--disable-negotiate-auth"
PACKAGECONFIG[nghttp2] = "--with-nghttp2,--without-nghttp2,nghttp2"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[random] = "--with-random=${RANDOM},--without-random"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[rtsp] = "--enable-rtsp,--disable-rtsp,"
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb,"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[telnet] = "--enable-telnet,--disable-telnet,"
PACKAGECONFIG[tftp] = "--enable-tftp,--disable-tftp,"
PACKAGECONFIG[threaded-resolver] = "--enable-threaded-resolver,--disable-threaded-resolver,,,,ares"
PACKAGECONFIG[verbose] = "--enable-verbose,--disable-verbose"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"

EXTRA_OECONF = " \
    --disable-libcurl-option \
    --disable-ntlm-wb \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libpsl \
    --enable-optimize \
    ${@'--without-ssl' if (bb.utils.filter('PACKAGECONFIG', 'gnutls mbedtls openssl', d) == '') else ''} \
"

do_install:append:class-target() {
	# cleanup buildpaths from curl-config
	sed -i \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's|${@" ".join(d.getVar("DEBUG_PREFIX_MAP").split())}||g' \
	    ${D}${bindir}/curl-config
}

do_compile_ptest() {
	oe_runmake -C ${B}/tests
}

do_install_ptest() {
	cat  ${WORKDIR}/disable-tests >> ${S}/tests/data/DISABLED
	rm -f ${B}/tests/configurehelp.pm
	cp -rf ${B}/tests ${D}${PTEST_PATH}
        rm -f ${D}${PTEST_PATH}/tests/libtest/.libs/libhostname.la
        rm -f ${D}${PTEST_PATH}/tests/libtest/libhostname.la
        mv ${D}${PTEST_PATH}/tests/libtest/.libs/* ${D}${PTEST_PATH}/tests/libtest/
        mv ${D}${PTEST_PATH}/tests/libtest/libhostname.so ${D}${PTEST_PATH}/tests/libtest/.libs/
        mv ${D}${PTEST_PATH}/tests/http/clients/.libs/* ${D}${PTEST_PATH}/tests/http/clients/
	cp -rf ${S}/tests ${D}${PTEST_PATH}
	find ${D}${PTEST_PATH}/ -type f -name Makefile.am -o -name Makefile.in -o -name Makefile -delete
	install -d ${D}${PTEST_PATH}/src
	ln -sf ${bindir}/curl   ${D}${PTEST_PATH}/src/curl
	cp -rf ${D}${bindir}/curl-config ${D}${PTEST_PATH}
}

RDEPENDS:${PN}-ptest += " \
	bash \
	perl-module-b \
	perl-module-base \
	perl-module-cwd \
	perl-module-digest \
	perl-module-digest-md5 \
	perl-module-file-basename \
	perl-module-file-spec \
	perl-module-file-temp \
	perl-module-io-socket \
	perl-module-ipc-open2 \
	perl-module-list-util \
	perl-module-memoize \
	perl-module-storable \
	perl-module-time-hires \
"

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS:lib${BPN} += "ca-certificates"

FILES:${PN} += "${datadir}/zsh"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/curl-config"

BBCLASSEXTEND = "native nativesdk"