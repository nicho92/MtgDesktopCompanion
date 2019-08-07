![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/logo_src.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/logo_src.png)

# Magic The Gathering Companion
Personal Magic the Gathering card manager Deck Builder and Collection Editor

[![GitHub issues](https://img.shields.io/github/issues/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/issues)
[![Build Status](https://travis-ci.org/nicho92/MtgDesktopCompanion.svg?branch=master)](https://travis-ci.org/nicho92/MtgDesktopCompanion)
[![GitHub forks](https://img.shields.io/github/forks/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/network)
[![GitHub stars](https://img.shields.io/github/stars/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/stargazers)
[![GitHub stars](https://img.shields.io/badge/download-2.8-green.svg)](https://github.com/nicho92/MtgDesktopCompanion/releases/)
[![GitHub stars](https://img.shields.io/twitter/url/https/shields.io.svg?style=social)](https://twitter.com/mtgdesktopcomp1)
[![PayPal](https://img.shields.io/static/v1.svg?label=PayPal&message=Support%20MTGCompanion&color=Blue&logo=paypal)](https://www.paypal.me/nicolaspihen)

# Launch
```
Need to have Java > 11 installed : (https://www.oracle.com/technetwork/java/javase/downloads/index.html)

download and unzip latest release [here](https://github.com/nicho92/MtgDesktopCompanion/releases)

go to /bin directory and launch mtg-desktop-companion.bat (for windows) or mtg-desktop-companion.sh (for unix)

```

# Setup from source
```
git clone (https://github.com/nicho92/MtgDesktopCompanion.git)

mvn -DskipTest clean install

cd target/executable/bin and launch mtg-desktop-companion.bat or mtg-desktop-companion.sh

```

# Website : 
[MTG Companion website](https://nicho92.github.io/MtgDesktopCompanion/)


# Features :

- Multi Engine : Scryfall, MTGJson,...
- Multi Database : MySQL, Postgres, Hsql, MongoDB,...
- Deck Editor (construct, sealed) and import tool from many websites (tappedout, deckstat,mtggoldfish,mtgTop8,...)
- Collection manager
- Prices analysis from many providers  (MTGStock, MTGOldfish,...)
- import / export decks and list cards to dozen formats (mtgo,dci sheet, csv, cockatrice,MagicCardMarket wantlist..) 
- Cards prices alerts
- Manacurve, colors and types repartition analysis
- Standalone servers (game room, console server, http server, price checking).
- New magiccardMarket Pricer : Stay tunned !!,  when you're alerted by a good bid for your wanted cards, it's automatically added to your cart's account ! 
- Manage your stock card, mass modification, import/export from deck, website. Update your Mkm Seller Account stock, Automaticaly update prices !
- Get alerted with many notifier (Telegram, mail, Discord,....) 
- Cross-plateform : Discord Bot, Plugin for Chrome,...
- Embedded webUI and JsonServer



# Portfolio

Main interface :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/magicSearch.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/magicSearch.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/magicThumbnail.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/magicThumbnail.png)


Get realtime prices on seller websites :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/cardsprices.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/cardsprices.png)


Deck Manager :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/deckManager.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/deckManager.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/deckManager2.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/deckManager2.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/import_deck.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/import_deck.png)


Collection Manager :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/collectionManager.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/collectionManager.png)

Manage your dashboard with your interested "dashlet"
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/dashboard.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/dashboard.png)

Check for price variation :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/priceVariation.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/priceVariation.png)

Use "MoreLikeThis" Functionnality :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/morelikethis.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/morelikethis.png)

import or export your card list / deck :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/import_export.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/import_export.png)

Be alerted for your wanted cards :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/alerts.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/alerts.png)

Manage your stock :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/stockManagement.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/stockManagement.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/stock.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/stock.png)

Store your financial movement and check your benefits :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/financial.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/financial.png)

Try your deck in game simulator :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/tryDeck.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/tryDeck.png)

Create deck in sealed format :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/sealed.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/sealed.png)

get your companion in discord's channels :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/discord.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/discord.png)

get your companion in chrome :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/mtgchromecompanion.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/mtgchromecompanion.png)


Generate Website of your collections :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/website.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/website.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/website2.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/website2.png)

Create your own cards :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/cardmaker.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/cardmaker.png)

Get news from your favorites sites :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/news.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/news.png)

Manage your card with the reponsive web UI :
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/web-ui.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/web-ui.png)
![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/web-ui-2.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/wiki/web-ui-2.png)


