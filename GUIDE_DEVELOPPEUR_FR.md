# Developer Guide — MTG Desktop Companion

This document explains the project architecture, the global business logic, and especially the role of **interfaces** and **abstract classes** to help developers ramp up quickly.

---

## 1) High-level overview

MTG Desktop Companion is a desktop-oriented Java monolith (Swing) with embedded services (web/API/jobs) and a strongly **plugin-oriented** architecture.

Main layers:

1. **Entry point / bootstrap** (`org.magic.main`): startup sequence, splash screen, config/plugin loading, GUI startup.
2. **Core services** (`org.magic.services`): global configuration, plugin registry, threading, i18n, cross-cutting tools.
3. **API contracts** (`org.magic.api.interfaces`): business interfaces for each plugin family.
4. **Implementation skeletons** (`org.magic.api.interfaces.abstracts`): abstract classes with shared default behavior.
5. **Concrete implementations** (`org.magic.api.*.impl`, `org.magic.servers.impl`): providers/pricers/DAO/exporters/etc.
6. **UI & servers** (`org.magic.gui`, `org.magic.servers`): Swing interface + embedded web/JSON/monitoring servers.

---

## 2) Startup execution flow

### Main entry point

Desktop startup runs through `MtgDesktopCompanion.main()`.

Simplified flow:

1. Logger and splash initialization.
2. Configuration loading through `MTGControler` (singleton).
3. `MTGControler` wires `PluginRegistry` with XML configuration.
4. Key plugins are loaded/activated (cards provider + DAO).
5. Main GUI (`MagicGUI`) is shown.
6. Enabled `MTGServer` plugins with `autostart` are started.

👉 In practice, **configuration drives everything**: active plugins, options, accounts, thread pool, and more.

---

## 3) Plugin architecture core

## 3.1 Base contract: `MTGPlugin`

`MTGPlugin` is the root interface of almost the entire plugin ecosystem.

Main responsibilities:

- lifecycle: `load()`, `save()`, `unload()`, `enable(boolean)`;
- metadata: `getName()`, `getType()`, `getVersion()`, `getStatut()`;
- configuration: `getProperties()`, `setProperty()`, `getDefaultAttributes()`;
- observability: `addObserver()`, `removeObserver()`, etc.;
- security/account integration: `needAuthenticator()`, `getAuthenticator()`.

### Plugin families

The `MTGPlugin.PLUGINS` enum includes:

- `PROVIDER`, `PRICER`, `DAO`, `SERVER`, `EXPORT`, `NEWS`, `PICTURE`,
- `CACHE`, `TOKEN`, `SCRIPT`, `POOL`, `COMBO`, `GRADING`,
- `TRACKING`, `EXTERNAL_SHOP`, `IA`, `SEALED`, `NETWORK`, and more.

This enum is used both for runtime organization and XML configuration mapping.

## 3.2 Root abstract class: `AbstractMTGPlugin`

`AbstractMTGPlugin` provides common mechanics:

- plugin config file management (`<conf_dir>/<type>/<plugin>.conf`);
- property loading/saving;
- `enabled/loaded` state handling;
- icon/documentation/observer notifications;
- default behavior shared by most plugin types.

➡️ In most cases, a new plugin should **extend a specialized abstract class**, which itself extends `AbstractMTGPlugin`.

## 3.3 `PluginRegistry`: central registry

`PluginRegistry` acts as the lightweight plugin container.

It:

- declares all plugin families (`init()`);
- maps interface ↔ implementation package ↔ XML paths;
- dynamically instantiates classes through reflection;
- loads configured plugins;
- handles missing/incompatible plugins by cleaning config entries.

The static helper `MTG` simplifies usage from the rest of the app:

- `getEnabledPlugin(MyType.class)`;
- `listEnabledPlugins(MyType.class)`;
- `getPlugin("Name", MyType.class)`.

---

## 4) Key interfaces and matching abstract classes

The project follows a very consistent pattern:

- **1 business interface** per domain,
- **1 base abstract class** with shared behavior,
- **N concrete implementations**.

Core examples:

- `MTGCardsProvider` → `AbstractCardsProvider` → concrete providers (Scryfall, etc.).
- `MTGPricesProvider` → `AbstractPricesProvider` → multi-source pricers.
- `MTGDao` → `AbstractMagicDAO` (+ `AbstractMagicSQLDAO`) → SQL/file DAO backends.
- `MTGServer` → `AbstractMTGServer` (+ `AbstractWebServer`/`AbstractWarServer`) → embedded servers.
- `MTGCardsExport` → `AbstractCardExport` (+ `AbstractFormattedFileCardExport`) → multi-format exports.

### Examples of shared business logic already implemented

- `AbstractCardsProvider`: cards/editions/languages cache, query helpers, booster generation.
- `AbstractPricesProvider`: price normalization + currency conversion + best-price selection.
- `AbstractMagicDAO`: cross-cutting utility operations (stocks, collections, duplication, announcements).
- `AbstractMTGServer`: embedded server cache + default timeout attributes.

**Important consequence:**
Most horizontal business logic lives in abstract classes; concrete implementations mostly focus on external source integration (API/site/DB/file).

---

## 5) Package layout (quick reading map)

- `src/main/java/org/magic/main`: executable entry points (desktop, server launcher, scripts UI).
- `src/main/java/org/magic/services`: technical core and orchestration.
- `src/main/java/org/magic/api/interfaces`: plugin contracts.
- `src/main/java/org/magic/api/interfaces/abstracts`: base implementation skeletons.
- `src/main/java/org/magic/api/*/impl`: domain implementations.
- `src/main/java/org/magic/servers/impl`: embedded server implementations.
- `src/main/resources`: default configs, assets, web UIs, i18n.
- `src/test/java`: unit/integration tests by provider domain.

---

## 6) Cross-cutting business logic focus

## 6.1 Configuration & global runtime state

`MTGControler` is the runtime singleton:

- creates/loads user `default-conf.xml`;
- initializes i18n (`LanguageService`), LAF, and thread pool;
- configures `PluginRegistry`;
- manages cross-cutting services (currency converter, webshop, version checker).

It effectively acts as a historical application service locator.

## 6.2 Data & storage

Primary storage is handled by the active `MTGDao` plugin.

`AbstractMagicDAO` already provides high-level behavior (stocks, announcements, collections, DAO-to-DAO duplication), reducing backend-specific boilerplate.

## 6.3 External integrations

The application integrates many connectors:

- card/price/image/news providers,
- exporters to external formats/platforms,
- parcel tracking, external shops, AI providers, etc.

Each connector remains interchangeable because it is constrained by a dedicated plugin interface.

## 6.4 Service exposure

`MTGServer` plugins expose embedded network capabilities (JSON API, admin dashboards, shopping/trades web UI, scheduled jobs, monitoring), with optional autostart.

---

## 7) How to add a new feature cleanly

## Case A — New connector (recommended)

1. Identify the family (`MTGPricesProvider`, `MTGNewsProvider`, etc.).
2. Extend the matching abstract class.
3. Implement only the required abstract business methods.
4. Declare config attributes with `getDefaultAttributes()`.
5. Add focused tests under `src/test/java/test/providers/...`.

## Case B — New cross-cutting family

If no existing family fits:

1. Create a new interface in `org.magic.api.interfaces`.
2. Create a dedicated abstract base class in `...interfaces.abstracts`.
3. Register the new family in `PluginRegistry.init()`.
4. Add any required UI/config integration.

---

## 8) Practical learning path for new developers

To understand the project quickly, follow this order:

1. `README.md` (global vision + build)
2. `MtgDesktopCompanion` (real bootstrap)
3. `MTGControler` (config/runtime)
4. `PluginRegistry` + `MTGPlugin` + `AbstractMTGPlugin`
5. One full interface→abstract→implementation chain (for example pricer)
6. Tests in your target domain

---

## 9) Architecture summary (TL;DR)

- **Plugin-first** architecture.
- `MTGPlugin` = universal contract.
- `AbstractMTGPlugin` = shared technical foundation.
- Specialized abstract classes already contain most shared business logic.
- Concrete implementations are mostly adapters to external sources.
- `PluginRegistry` + `MTGControler` orchestrate the runtime.

This is a mature, extensible base with a significant initial discovery cost (many modules), but strong functional scalability once the pattern is understood.
