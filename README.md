# Magic The Gathering Desktop Companion

[![GitHub stars](https://img.shields.io/badge/download-latest-green.svg)](https://github.com/nicho92/MtgDesktopCompanion/releases/)
[![GitHub issues](https://img.shields.io/github/issues/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/issues)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.magic%3Amagic-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.magic%3Amagic-api)
[![GitHub forks](https://img.shields.io/github/forks/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/network)
[![GitHub stars](https://img.shields.io/github/stars/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/stargazers)
[![GitHub stars](https://img.shields.io/twitter/url/https/shields.io.svg?style=social)](https://twitter.com/mtgdesktopcomp1)

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

## Support

If you want to support hosting and ongoing development, see donation/support links on the website and in the release pages.
MTGCompanion is developped with many contributors, and is and will remain open source. Online service ( like Discord bot, Websites) are hosted on my own server, and DNS are paid on my a personnal funds.
Any help are welcome :) [![PayPal](https://img.shields.io/static/v1.svg?label=PayPal&message=Support%20MTGCompanion&color=Blue&logo=paypal)](https://www.paypal.com/donate/?business=ZXJKNZZQ2S7US&no_recurring=0&item_name=Help+me+to+continue+MTGCompanion+developpement%2C+and+online+service+like+Discord+bot+&currency_code=EUR)

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


## Portfolio

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/1.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/2.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/3.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/4.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/5.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/6.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/7.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/8.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/9.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/10.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/11.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/12.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/13.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/14.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/15.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/16.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/17.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/18.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/19.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/20.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/21.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/22.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/23.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/24.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/25.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/26.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/27.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/28.png)

![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/29.png)




