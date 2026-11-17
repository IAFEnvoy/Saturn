# Saturn

Saturn is a NeoForge library for data-driven mods. It provides an extensible ability system, resource management,
a customizable HUD framework, and shared player views and information displays.

## Features

- **More Codecs**: Support for additional data formats and serialization methods, making it easier to work with various
  types of data.
- **Actions & Conditions**: Execute an action or check a condition, with a full set of built-in types and the ability to
  add your own. (Inspired by Origins)
- **Costs**: Define costs for abilities, including item consumption, resource usage, and custom conditions.
- **Formulas**: Create complex calculations for abilities, costs, and conditions using a flexible formula system. Based
  on exp4j.
- **Storage**: Store and retrieve data for abilities, conditions, and other game elements.
- **Timelines**: Schedule actions and events over time, with support for delays and intervals.
- **Triggers**: Define triggers that respond to in-game events, such as player actions or environmental changes. (
  Inspired by KubeJS)
- **Abilities**: Create custom abilities with unique effects, costs, and triggers, including support for passive
  abilities and resource management.
- **Resources & Bars**: Manage custom resources and display them on the HUD with customizable resource bars.
- **Wheel**: Native selection and configuration screens, a combined scrollable candidate pool, twelve saved slots,
  derived pages, a HUD grid, server-validated activation, and registrable content kinds, sources, and tooltips.
- **HUD Framework**: A flexible HUD system that allows you to create and customize HUD elements for your abilities and
  resources.
- **Client-side Features**: Includes a drag-and-drop HUD editor, resource bar rendering, and support for custom HUD
  elements.
- **Player Views**: Parse supplied players, saved NBT, or snapshots into detached, extensible views shared by screens,
  commands, and cards.
- **Data-driven Information**: Define information rows in JSON using parameterized entity getters and expressions for
  numbers, text, meters, lists, tables, and conditions. Groups select rows through direct references or tags, with
  support for multiple groups, deduplication, and importance filtering.
- **Profiling**: Built-in profiling tools to help you optimize your mod's performance and identify potential
  bottlenecks.
- **KubeJS Integration**: Seamless integration with KubeJS for scripting and extending the library's functionality.

## Controls

| Action                             | Default                                                 |
|------------------------------------|---------------------------------------------------------|
| Open the ability wheel             | Hold `R`                                                |
| Use the selected entry             | `V`                                                     |
| Previous / next page               | Left / right arrow; mouse wheel while the wheel is open |
| Configure the wheel                | `/wheel` or `/wheel configure`                          |
| Use slots 1–12 on the current page | Unbound                                                 |
| Edit HUD layout                    | Right `Shift`                                           |

Key bindings can be changed in Minecraft's controls. Slots 1–12 are under **Saturn: Wheel Slots**; all other
bindings are under **Saturn**. The wheel configuration key is unbound by default.

In the wheel editor, select an entry and left-click a slot to assign it. Right-click a slot to clear it.
Closing the editor saves your layout.

The main wheel starts empty. When all twelve main slots are empty, the HUD hides only that page; automatically
discovered entries remain visible. Assigning a main slot shows the main page again.

Hold or toggle opening, mouse-wheel paging, and page wrapping can be adjusted in Saturn's client configuration.

## Wiki

See the [Saturn Wiki](https://docs.iafenvoy.com/docs/library/saturn) for guides, configuration, and development details.
