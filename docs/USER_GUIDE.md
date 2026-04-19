# User Guide

This guide covers the common workflows for Magic The Gathering Desktop Companion.

## 1. Installation options

### Option A: Prebuilt release

1. Download the latest archive from GitHub releases.
2. Unzip it.
3. Open the `bin/` folder and run:
   - `mtg-desktop-companion.bat` (Windows)
   - `mtg-desktop-companion.sh` (Linux/macOS)

### Option B: Build from source

```bash
git clone https://github.com/nicho92/MtgDesktopCompanion.git
cd MtgDesktopCompanion
mvn -DskipTests clean install
```

Then run the generated script in `target/executable/bin/`.

## 2. First launch checklist

- Verify Java 23+ is installed and available in your `PATH`.
- Start the app and open the preferences/configuration panel.
- Configure your preferred providers (cards, prices, caches, notifications).
- Save your configuration before importing data.

## 3. Core workflows

### Manage your collection

- Import cards into stock.
- Update condition/foil/etched values.
- Export inventory for reporting or marketplace sync.

### Build decks

- Create or import decklists.
- Analyze mana curve and card distribution.
- Export to your target format (CSV, deck sites, marketplace formats).

### Analyze prices

- Select one or more price providers.
- Open dashboards to compare trends.
- Configure alerting rules to detect opportunities.

### Use embedded web services

Depending on your setup, the app can expose services for pricing, trading, shopping, and collection browsing through embedded servers.

## 4. Troubleshooting

### Application does not start

- Check Java version (`java -version`).
- Re-run build with verbose Maven logs.
- Confirm startup script has execute permission on Unix systems.

### Provider data is missing

- Ensure network connectivity.
- Validate provider configuration and credentials (if required).
- Try switching to an alternate provider for comparison.

### Export/import issues

- Validate file encoding (UTF-8 recommended).
- Check separator/format compatibility (CSV vs deck format).

## 5. Learn more

- Project wiki: <https://github.com/nicho92/MtgDesktopCompanion/wiki>
- Website: <https://www.mtgcompanion.org/>
