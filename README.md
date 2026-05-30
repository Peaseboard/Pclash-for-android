# Pclash Android

A modern, open-source proxy client for Android, built on the Mihomo (Clash.Meta) core.

## Features

- **Full Protocol Support**: SS, VMess, VLESS, Trojan, Hysteria, Hysteria2, TUIC, ShadowTLS, WireGuard, and more
- **Mihomo Core**: Latest pre-built binaries auto-downloaded during build
- **Subscription Management**: Import from URL or file, auto-update support
- **Material Design**: Clean, intuitive UI following Android design guidelines
- **TUN Mode**: Full system traffic routing via VpnService
- **Access Control**: Per-app proxy control

## Building

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## License

See [LICENSE](LICENSE)
