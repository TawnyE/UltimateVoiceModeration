# Ultimate Voice Moderation

A fully server-side voice and text moderation system for Minecraft Java Edition.

## Features

- **Offline by default:** All moderation is performed on the server, with no external API calls required.
- **Voice and text moderation:** Moderates both spoken and written chat.
- **Multi-layered detection:** Uses a combination of exact matching, regex patterns, and fuzzy matching to detect violations.
- **Configurable actions:** A wide range of actions can be configured for each violation category, including warnings, mutes, kicks, and running console commands.
- **Folia support:** Designed to be compatible with both standard Bukkit/Paper servers and Folia servers.
- **Performance-focused:** The plugin has been designed to be as lightweight as possible, with all intensive tasks running asynchronously to avoid impacting server performance.

## Installation

1.  Download the latest release from the [releases page](https://github.com/your-repo/ultimate-voice-moderation/releases).
2.  Place the downloaded `.jar` file into your server's `plugins` directory.
3.  Install a compatible voice chat plugin, such as [Simple Voice Chat](https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat).
4.  Start your server. The `UltimateVoiceModeration` directory will be created in your `plugins` directory, containing the `config.yml` and `messages.yml` files.
5.  Configure the plugin to your liking by editing the `config.yml` and `messages.yml` files.
6.  Restart your server or use the `/uvm reload` command to apply the changes.

## Configuration

The `config.yml` file is extensively documented, allowing you to customize every aspect of the plugin. Here are some of the key settings:

-   `enabled`: Toggles the entire plugin on or off.
-   `voice.enabled`: Toggles voice moderation on or off.
-   `text.enabled`: Toggles text moderation on or off.
-   `providers`: Configure the transcription and moderation providers. By default, the plugin uses a local, offline-only provider.
-   `categories`: Configure the moderation categories, including the actions to be taken for each violation, the mute duration, and any console commands to be run.
-   `patternLists`: Add your own banned words, regex patterns, and soft-banned words to the moderation engine.
-   `performance`: Fine-tune the performance of the plugin to match your server's hardware.
-   `logging`: Configure the audit logging settings.

## Enabling External Providers

By default, Ultimate Voice Moderation uses a local, offline-only moderation engine. However, the plugin can be configured to use external APIs for transcription and moderation. To do so, you will need to change the `providers.transcription.type` and `providers.moderation.type` settings in the `config.yml` file to `external_api`, and then fill in the required API keys and endpoints in the corresponding sections.

**Note:** Using external providers may incur additional costs and will require an internet connection.

## Contributing

Contributions are welcome! If you have any ideas, suggestions, or bug reports, please open an issue or submit a pull request on the [GitHub repository](https://github.com/your-repo/ultimate-voice-moderation).
