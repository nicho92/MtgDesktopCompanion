# Magic The Gathering Desktop Companion

Magic The Gathering Desktop Companion is an open-source Java application to manage your MTG collection, build decks, and analyze prices across multiple providers.

## Highlights

- Collection and stock management (foil/etched/condition aware)
- Deck building (constructed and sealed) with import/export tools
- Price analysis dashboards and alerts
- Embedded services (web UI, JSON HTTP server, shopping/trades/collection web servers)
- Plugin-oriented architecture for providers (cards, cache, notifications, external shops, dashboards, and more)

## Project links

- Website: <https://www.mtgcompanion.org/>
- Releases: <https://github.com/nicho92/MtgDesktopCompanion/releases>
- Wiki: <https://github.com/nicho92/MtgDesktopCompanion/wiki>
- Issues: <https://github.com/nicho92/MtgDesktopCompanion/issues>
- Security policy: [SECURITY.md](SECURITY.md)

## Requirements

- Java 23+
- Maven 3.9+

## Quick start (from source)

```bash
git clone https://github.com/nicho92/MtgDesktopCompanion.git
cd MtgDesktopCompanion
mvn -DskipTests clean install
```

Then launch the packaged application from:

```bash
target/executable/bin/mtg-desktop-companion.sh
```

(Use `mtg-desktop-companion.bat` on Windows.)

## Build and test

```bash
mvn clean install
mvn test
```

## Documentation

- User guide: [docs/USER_GUIDE.md](docs/USER_GUIDE.md)
- Developer guide: [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)


## Repository structure

- `src/main/java/org/magic/api`: provider APIs and plugin implementations
- `src/main/java/org/magic/services`: core services (business logic, jobs, tools, networking)
- `src/main/java/org/magic/servers`: embedded server implementations
- `src/test/java`: unit and integration tests

## Contributing

1. Fork the repository.
2. Create a feature branch.
3. Run tests locally.
4. Open a pull request with a clear description and reproduction steps (if bug fix).

## Support

If you want to support hosting and ongoing development, see donation/support links on the website and in the release pages.
