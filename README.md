![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/icons/logo_src.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/icons/logo_src.png)

# Magic The Gathering Companion
Personal Magic the Gathering card manager Deck Builder, Collection Editor and prices scrapper

[![GitHub stars](https://img.shields.io/badge/download-latest-green.svg)](https://github.com/nicho92/MtgDesktopCompanion/releases/)
[![GitHub issues](https://img.shields.io/github/issues/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/issues)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.magic%3Amagic-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.magic%3Amagic-api)
[![GitHub forks](https://img.shields.io/github/forks/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/network)
[![GitHub stars](https://img.shields.io/github/stars/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/stargazers)
[![GitHub stars](https://img.shields.io/twitter/url/https/shields.io.svg?style=social)](https://twitter.com/mtgdesktopcomp1)

# Help
MTGCompanion is developped with many contributors, and is and will remain open source. Online service ( like Discord bot, Websites) are hosted on my own server, and DNS are paid on my a personnal funds.
Any help are welcome :) [![PayPal](https://img.shields.io/static/v1.svg?label=PayPal&message=Support%20MTGCompanion&color=Blue&logo=paypal)](https://www.paypal.com/donate/?business=ZXJKNZZQ2S7US&no_recurring=0&item_name=Help+me+to+continue+MTGCompanion+developpement%2C+and+online+service+like+Discord+bot+&currency_code=EUR)

# Website : 
[MTG Companion website](https://www.mtgcompanion.org/)

[MTG Web pricer Servers](https://my.mtgcompanion.org/prices-ui)

[MTG Web Shop Servers](https://my.mtgcompanion.org/shop-ui)

[MTG Web Trader Servers](https://my.mtgcompanion.org/trades-ui/pages/index.html)

[MTG Web Collection Servers](https://my.mtgcompanion.org/collection-ui/pages/index.html)

[MTG Discord Bot](https://top.gg/bot/448196866774007808) 

[Docker Web UI](https://hub.docker.com/r/mtgcompanion/mtgcompanion/tags)

[AUR Package](https://aur.archlinux.org/packages/mtg-desktop-companion) thanks to @LuckyTurtleDev

# Configure JAVA 

Need to have Java >=23 installed : https://www.oracle.com/technetwork/java/javase/downloads/index.html

and java.exe is in your PATH : [HOW TO](https://www.baeldung.com/java-home-on-windows-mac-os-x-linux) 



# Launch
```
 Download and unzip latest release at https://github.com/nicho92/MtgDesktopCompanion/releases

 go to /bin directory and launch mtg-desktop-companion.bat (for windows) or mtg-desktop-companion.sh (for unix)
```

# Setup from source

 Need to have Maven installed : https://maven.apache.org/download.cgi


```
git clone https://github.com/nicho92/MtgDesktopCompanion.git

mvn -DskipTests clean install

cd target/executable/bin and launch mtg-desktop-companion.bat or mtg-desktop-companion.sh

```

# Features :

- Multi Engine : Scryfall, MTGJson,...
- Multi Database : MySQL, Postgres, Hsql, MongoDB,...
- Deck Editor (construct, sealed) and import tool from many websites (tappedout, deckstat,mtggoldfish,mtgTop8,...)
- Collection manager (stock, foil, etched, condition,...) 
- Thematic Dashboards : personnalize your interested PKI in multiple dashboard.
- Prices analysis from many providers  (MTGStock, MTGOldfish,...)
- import / export decks and list cards to dozen formats (mtgo,dci sheet, csv, cockatrice,MagicCardMarket wantlist..) 
- Cards prices alerts
- Manacurve, colors and types repartition analysis
- Standalone servers (http server, price checking, JsonServer, Online COmmerce, Online Trading, Online Catalog).
- New magiccardMarket Pricer : Stay tunned !!,  when you're alerted by a good bid for your wanted cards, it's automatically added to your cart's account ! 
- Manage your stock card, mass modification, import/export from deck, website. Update your Mkm Seller Account stock, Automaticaly update prices !
- Get alerted with many notifier (Telegram, mail, Discord,....) 
- Cross-plateform : Discord Bot, Plugin for Chrome,...
- Embedded webUI and JsonServer
- Embedded webshop server
- Try discord bot : https://discord.com/api/oauth2/authorize?client_id=448196866774007808&permissions=0&scope=bot
- Use IA to create your card, generate decks, ...

# How TO
See manual in [wiki section](https://github.com/nicho92/MtgDesktopCompanion/wiki)

# Portfolio

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


[![Star History Chart](https://api.star-history.com/svg?repos=nicho92/MtgDesktopCompanion&type=Date)](https://star-history.com/#nicho92/MtgDesktopCompanion&Date)
