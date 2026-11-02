# Saturn

Saturn is a NeoForge library for data-driven mods. It provides an extensible ability system, resource management,
a customizable HUD framework, and shared player views and information displays.

Requires Minecraft **26.1.2**, NeoForge **26.1.2.99**, and Java **25**. KubeJS integration is optional.

## Features

- More Codecs: Support for additional data formats and serialization methods, making it easier to work with various types of data.
- Actions & Conditions: Execute an action or check a condition, with a full set of built-in types and the ability to add
  your own. (Inspired by Origins)
- Costs: Define costs for abilities, including item consumption, resource usage, and custom conditions.
- Formulas: Create complex calculations for abilities, costs, and conditions using a flexible formula system. Based on
  exp4j.
- Storage: Store and retrieve data for abilities, conditions, and other game elements.
- Timelines: Schedule actions and events over time, with support for delays and intervals.
- Triggers: Define triggers that respond to in-game events, such as player actions or environmental changes. (Inspired
  by KubeJS)
- Abilities: Create custom abilities with unique effects, costs, and triggers, including support for passive abilities
  and resource management.
- Resources & Bars: Manage custom resources and display them on the HUD with customizable resource bars.
- HUD Framework: A flexible HUD system that allows you to create and customize HUD elements for your abilities and
  resources.
- Client-side Features: Includes a drag-and-drop HUD editor, resource bar rendering, and support for custom HUD
  elements.
- Player Views: Parse supplied players, saved NBT, or snapshots into detached, extensible views shared by screens,
  commands, and cards.
- Data-driven Information: Define information rows in JSON using parameterized entity getters and expressions for numbers,
  text, meters, lists, tables, and conditions. Groups select rows through direct references or tags, with support for
  multiple groups, deduplication, and importance filtering.
- Profiling: Built-in profiling tools to help you optimize your mod's performance and identify potential bottlenecks.
- KubeJS Integration: Seamless integration with KubeJS for scripting and extending the library's functionality.

## Player Views and Information Rows

Capture a view from a supplied player, then collect all information or select a group:

```java
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.PlayerViews;

var view = PlayerViews.parse(player);
var all = PlayerInformation.collectAll(view);
var basic = PlayerInformation.collect(view, PlayerInformation.BASIC);
var compact = all.withMinimumImportance(60);
```

Saturn parses the data supplied by the host mod. The host handles player-file access, archive storage, permissions,
and player-data networking. Views distinguish missing information from known zero values and empty collections.

Each `EntityValueGetter<T>` owns a parameter `MapCodec`, a result `valueCodec`, a schema (default `1`), and optional
`capture(Entity)` / `readSaved(SavedPlayerViewContext)` methods. Mods register parameter codecs through
`DeferredRegister` against `SaturnRegistries.ENTITY_VALUE_GETTER`. The 26 built-in getter types live under
`data.information.getter.builtin`, including queries for a specific attribute, resource, effect, or score.
`PlayerViews` collects default queries, requested queries, and the queries used by enabled information rows, isolating
each query's failures. Snapshots distinguish queries by their complete parameters and retain the existing basic fields.

Rows define their own content, order, and `importance` (default `0`; higher means more important). **Groups reference
rows**, using `Either<Holder<InformationRow>, TagKey<InformationRow>>`. `InformationRow` is a definition;
`InformationRowResult` is the evaluated display result. For example, a group at
`data/example/saturn/information_group/overview.json` can select one row and a tag:

```json
{
  "title": { "translate": "info.example.group.overview" },
  "order": 0,
  "rows": ["saturn:health", "#saturn:status"]
}
```

Row definitions live under `data/<namespace>/saturn/information_row/`; row tags live under
`data/<namespace>/tags/saturn/information_row/`. A row can be selected by several groups. Group queries expand and
deduplicate references; collecting all rows also includes ungrouped rows. Row definitions do not declare groups.

The library includes **16 information rows**, **3 groups**, and their row tags. Packs can extend group contents through
tags or override definitions. Expressions read registered getters through their own result codecs and compose presentation
values; mods can register additional expression types with a `MapCodec`. Java row providers remain available.

| Registry | Purpose |
| --- | --- |
| `saturn:entity_value_getter` | Code registry for parameter codecs of typed live/saved-data queries |
| `saturn:information_expression_type` | Code registry for expression types |
| `saturn:information_row` | Datapack registry for row definitions |
| `saturn:information_group` | Datapack registry for groups and their row/tag references |

Definitions follow the native world-loading registry lifecycle; reopen the world or restart the server after editing
them. See the [data-driven information guide](docs/INFORMATION-DATA.md) for JSON formats, expression types, registry
contexts, and migration details.

Operators can inspect the same text projection from a command:

```mcfunction
/saturn information @s
/saturn information @s saturn:basic
```

`target` accepts a player name, UUID, or single-entity selector. Omitting the group collects all rows; group IDs support
completion. Results are sent only to the command source. The command requires gamemaster permission and returns the
number of logical rows. Command headings and messages include English and Simplified Chinese translations.

## Documentation

- [Public API and extension points](docs/API.md)
- [Player views, snapshots, and display API](docs/PLAYER-VIEWS.md)
- [Data-driven information rows, groups, tags, and expressions](docs/INFORMATION-DATA.md)
