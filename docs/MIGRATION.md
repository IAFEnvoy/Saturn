# 从 MiXianTu 拆出 Saturn：迁移说明

> **这份文档回答什么**：MiXianTu 的哪一块被复制进了 Saturn、改成了什么形状、MiXianTu 侧以后要改哪些东西。
> **基准**：Saturn 当前工作树（`gradlew.bat compileJava` 已通过）；MiXianTu 侧尚未改动，仍是 `com.iafenvoy.mxt`。
> **未验证**：没有跑 GameTest、没有实机、没有加载过数据包；本轮只到"编译通过"。

---

## 0. 现在是什么状态

| 项 | 数量 |
|---|---|
| Java 文件 | 547（复制 498、Saturn 侧新写 49），共 23 325 行 |
| 内置注册表条目 | 已注册（§5 全表），`regen` / `aura_pulse` 与内容 trigger 信号不搬 |
| lang | `assets/saturn/lang/{zh_cn,en_us}.json`，81 条 |
| KubeJS | `compileOnly`（`26.1.2-8.0.4`）；插件 + 5 组通用绑定已搬，内容绑定与 trigger 绑定留本体 |
| 指令 | `/saturn` 一棵树（§3 第 15 条） |
| 技能与触发器 | 运行时、内置技能类型（10 种）、通用事件、AbilityHooks、Sources、EntityTickStages 都在；word/storage/light_control 与 CultivationTriggerService 留在内容侧 |
| 编译 | `gradlew.bat compileJava` **通过** |
| 逐文件改名对照 | [RENAME-TABLE.md](RENAME-TABLE.md) |

Saturn 是**纯代码库**：没有任何内置定义（JSON）、没有测试包。注册表、条目、语言、HUD（含编辑键位）、指令与脚本桥都在；`aura`、整个轮盘、品质、profiler 界面与网络是明确不做的。

---

## 1. 一次性改名（D1：id 统一为 `saturn`）

| 项 | 旧（MiXianTu） | 新（Saturn） |
|---|---|---|
| 模组 id / 注册表命名空间 | `mxt` | `saturn` |
| Java 包 | `com.iafenvoy.mxt` | `com.iafenvoy.saturn` |
| 类名前缀 | `Mxt*` | `Saturn*` |
| 主类 | `MiXianTu`（`MiXianTu.MOD_ID`） | `Saturn`（`Saturn.MODID`，`MOD_ID` 是同值别名） |
| 代码注册表 id | `mxt:entity_action_type` 等 23 个 | `saturn:*` |
| 注册表默认条目 id | `mxt:no_op` / `mxt:always` / `mxt:constant` / `mxt:empty` / `mxt:missing` / `mxt:self` / `mxt:self_hud` / `mxt:use` / `mxt:resource`(cost) | `saturn:*` |
| 数据包注册表 id | `mxt:resource` / `mxt:ability` / `mxt:trigger` / `mxt:incomplete` | `saturn:*` |
| 数据文件目录 | `data/<命名空间>/mxt/<注册表>/<条目>.json` | `data/<命名空间>/saturn/<注册表>/<条目>.json` |
| lang 键前缀 | `mxt.*` | `saturn.*` |
| 附件 id | `mxt:resource_holder` / `mxt:ability_holder` | `saturn:*` |
| 界面入口 | `key.mxt.*` / `screen.mxt.*` / `hud.mxt.*`；轮盘的 `key.mxt.wheel*` 与 `wheel.mxt.*` 原样不动 | `key.saturn.*`（只有 `hud_layout`）/ `screen.saturn.*` / `hud.saturn.*` |
| 上下文扩展键 | `mxt:origin` | `saturn:origin` |
| HUD 布局文件 | `config/<modid>/mxt-hud.json` | `config/saturn/hud.json` |

**数据文件路径只跟注册表 key 的命名空间走，不跟条目 id 走。** 一个条目可以仍然叫 `mxt:my_action`，但文件必须放在 `data/<包>/saturn/entity_action_type/` 下。MiXianTu 要搬的目录：自带 27 个 JSON（`src/main/resources/data/mxt/mxt/**`）+ 测试包 392 个夹具（`src/test-mod/resources/data/mxt_test/mxt/**`）。

---

## 2. API 变更

### 2.1 删除（因为 aura 不进前置）

| 被删的东西 | 原来在哪 | MiXianTu 侧怎么办 |
|---|---|---|
| `Costs`（`validateAuras` / `auras(...)`） | `data/cost/Costs.java` | 留在 MiXianTu，换成自己的类 |
| `AuraCostSources` | `data/cost/context/AuraCostSources.java` | 留在 MiXianTu |
| `CostTarget.AuraStore` | `data/cost/CostTarget.java` | 删掉；aura 支付用自己的 `CostTarget` 实现（接口已开放） |
| `CostContext.auraSource`、`pool(...)`、`bank(...)`、`withAuraSource(...)` | `data/cost/context/CostContext.java` | 改用 `with("mxt:aura_source", source)`，`Cost` 实现里 `get(...)` 取回 |
| `CostPayment.auras()`、`PaymentResult.auras()`、`beyondResources()` 的 aura 分支 | `data/cost/CostPayment.java` | 不需要 |
| `CostFailure.INSUFFICIENT_AURA` | `runtime/resulting/failures/CostFailure.java` | 用自己命名的 failure 常量 |
| `api/AuraAccess`、`api/AuraCostSource` | `api/` | 不进 Saturn；MiXianTu 保留原样 |
| `Ability.elementAffinity` / `elementAffinityMode` / `AffinityMode` | `data/ability/Ability.java` | 元素亲和改用 MiXianTu 自己的条件或自定义 `AbilityType` |
| `EntityVariable` 的 `element_count` / `element_*` | `util/formula/variable/EntityVariable.java` | 改用 MiXianTu 自己的 `FormulaVariable` |
| `RegistryCaches` 的 16 个内容缓存与内容校验 | `registry/cache/RegistryCaches.java` | 留在 MiXianTu 自己的 `RegistryCaches` |
| `ResourceBarOverlay` 的灵气 HUD 与"资源→灵气"使用门 | `screen/resourcebar/ResourceBarOverlay.java` | 灵气条留在 MiXianTu，作为自己的 `HudEntry` 注册 |
| `ResourceTransactions` 的 aura 使用门 | `runtime/resource/ResourceTransactions.java` | 留在 MiXianTu 的 `ResourceUseService` |
| `regen` 资源值提供者 | `data/resource/builtin/RegenResourceValueProvider.java` | 读灵气回复速率；Saturn 只留 `current`/`max`/`missing`/`constant` |
| `aura_pulse` 数据存储 | `data/storage/builtin/AuraPulse.java` | 灵气脉冲，不进 Saturn |
| profiler 的网络与界面 | `network/payload/Profiler*`、`ProfilerSubscriptions`、`screen/profiling/*` | Saturn 不提供界面与数据传输，只保留采样核心与 `/saturn debug` |
| `FailureSources` 的 13 个内容来源 | `runtime/resulting/FailureSources.java` | Saturn 只剩 `COMMON` / `ABILITY` / `RESOURCE` |

### 2.2 没有复制（留在 MiXianTu）

| 类别 | 例子 | 理由 |
|---|---|---|
| 内容 builtin | action 21、condition 34（灵气、修为、灵根体质、诅咒、品质、阵法、秘境、丹药…） | 都调 MiXianTu 的服务 |
| 御剑 / 坐骑技能 | `MountAbilityType`、`FlightDisplay`、`MountRender`、`MountPose`、`ItemMountRender`、`mount_render_type` 注册表 | 御剑是内容玩法，Saturn 不提供 `mount` 类型与外观注册表；内容侧自带类型与 GeckoLib 外观 |
| 技能类型与渲染 | 剩下 3 种（`word` / `storage` / `flight_control`）、GeckoLib 外观 | 依赖内容运行时与渲染库 |
| 轮盘（整块） | 键位、`WheelSelectionState` / `WheelSelectionEntry`、`runtime/wheel/*`、`screen/wheel/*`、`WheelMenuScreen`、内容页、HUD 网格与贴图 | 与页面内容强绑定；Saturn 只留通用 HUD 框架与键位持有类 |
| KubeJS 内容绑定 | 事件转发、`MxtKubeJsEventDispatcher`、ability/cultivation/curse/aura/element/spirit_root/physique/technique/progression/quality/lifespan/soul/loot 绑定 | 依赖 Saturn 没有的事件层与玩法运行时；Saturn 只搬 5 组通用绑定 + 插件外壳 |
| 质量条目 | `util/matcher/builtin/QualityEntry` | 依赖 `QualityRequirement` |
| 灵气相关 | `data/aura/*`、`AuraClientState`、内容缓存 | D2 决定 aura 不进前置 |
| 内容值类型 | `data/cultivation|item|quality|progression/*` | 世界观定义 |
| 资源运行时的一半 | `ResourceContainerEntry` / `ResourceRegenStage` / `ResourceUseService` / `VesselPour*` | 依赖物品组件、修炼方式、灵液容器 |
| 物品宿主组件 | `mxt:storage` | Saturn 没有物品侧存储 |

### 2.3 改写（形状变了）

| 文件 | 改了什么 |
|---|---|
| `data/cost/context/CostContext.java` | record 多一个 `Map<String, Object> data`；新增 `with(key, value)` / `get(key)`；删掉 aura 三件 |
| `data/cost/CostTarget.java` | `sealed` → 普通接口（附属可加目标） |
| `data/cost/CostPayment.java` | 去掉 aura 投影与分支 |
| `data/ability/Ability.java` | 去掉元素亲和；JSON 少两个字段 |
| `util/formula/variable/EntityVariable.java` | 去掉元素分支 |
| `registry/cache/RegistryCaches.java` | 只剩 `ATTRIBUTE_NAMES` / `RESOURCE_NAMES` |
| `util/profiling/ServerProfiling.java` | `Source` 从枚举改成可注册桶（`register` / `all` / `byPath`），session 用注册序号；删订阅扇出与网络 |
| `screen/resourcebar/ResourceBarOverlay.java` | 只收 `saturn:resource` 的条 |
| `runtime/resource/ResourceTransactions.java` | 去掉 aura 使用门 |
| `screen/hud/HudLayoutFile.java` | 落盘 `config/saturn/hud.json` |
| `compat/kubejs/callback/SaturnJsCallbacks.java` | 只清 Saturn 有的六张回调表 |
| `registry/SaturnResourceKeys.java` | 23 个代码 key + 4 个数据包 key（原 56） |
| `registry/SaturnRegistries.java` | 23 个 `DefaultedRegistry` + `all()`（原 30） |
| `registry/SaturnDatapackRegistries.java` | 只注册 `resource` / `ability` / `trigger` / `incomplete`（原 43） |
| `registry/SaturnAttachments.java` | 只注册 `resource_holder` / `ability_holder` |
| `config/SaturnClientConfig.java` | 改成读 `SaturnConfig` 的 `ModConfigSpec` 客户端文件（`show_names` / `icons_in_center`） |
| `runtime/resulting/failures/CostFailure.java` | 去掉 `INSUFFICIENT_AURA` |
| `runtime/ability/AbilityService.java` | 删掉元素亲和分支（`Ability` 不再有该字段），伤害倍率与准则改走 `AbilityHooks`，`word` 校验改走 `AbilityHooks.validate` |
| `runtime/ability/AbilityEventBridge.java` | 品质门、法器技能、Curios 装备三处改走 `AbilityHooks` |
| `runtime/Sources.java` | `Grant` / `Declaration` 从枚举改成 record（路径 + 常量），内容可加自己的类别 |
| `registry/cache/RegistryCaches.java` | 加 `TRIGGER_RULES_BY_SIGNAL`（`saturn:trigger` 的索引） |
| `registry/SaturnAttachments.java` | 加 `TRIGGER_COOLDOWNS`（服务端专用） |
| `runtime/resulting/failures/AbilityFailure.java` | 去掉 `ELEMENT_AFFINITY` |
| `util/ClientPlayerNames.java` | 问服务端改成 `api/PlayerNameQuery` 钩子（Saturn 没有网络包） |
| `util/InventoryUtil.java` | Javadoc 去掉内容侧的引用 |
| `registry/SaturnContent.java` | 只剩汇总表：每个注册表的内置条目拆到 `registry/content/*`（23 个类），主类构造时统一注册；内置条目从 47 条扩到 288 条 |

### 2.4 新增（Saturn 侧）

`Saturn`（主类）、`registry/content/*`（每个注册表一个内置条目类，`SaturnContent` 是汇总表）、`registry/SaturnKeyMappings`（HUD 编辑键位与 `KeyMappingHolder`）、`command/SaturnCommands` + `SaturnDebugCommand` + `SaturnAbilityCommand` + `SaturnTriggersCommand` + `SaturnResourceCommand` + `command/client/{SaturnClientCommands,SaturnHudCommand}`、`compat/kubejs/{SaturnKubeJsPlugin,SaturnKubeJsApi}`、`api/AbilityHooks`、`runtime/ability/*`、`runtime/trigger/*`、`event/*`；资源 `assets/saturn/textures/gui/incomplete.png`、`assets/saturn/lang/*.json`、`kubejs.plugins.txt`。

本轮新增的通用件：`api/PlayerNameQuery`（名字查询钩子）、`loot/{HasAbilityLootCondition,GrantAbilityLootFunction}` + `registry/SaturnLootRegistries`（`saturn:has_ability` / `saturn:grant_ability`）、`util/dispatcher/{TickDispatcher,TickInterval,TickExecutionLimit}`、`util/{CloseableResources,ClientRenderResources,ClientLevelAccess,InventoryUtil,TooltipText,PlayerNames,ClientPlayerNames}`、`data/DescribedEntry`、`api/ItemUseCycle` + `event/ResolveItemUseCycleEvent`（通用长按手势，`mixin/ItemUseCycleMixin` 由 `saturn.mixins.json` 登记）、`runtime/hold/{ItemUseCycles,ItemUseCyclesClient}`、`advancement/IdentifierCriterionTrigger`（按 id 的准则）、`api/AliasManager` + `registry/SaturnAliases`（旧 id 映射）、`SaturnClient`（客户端初始化：注册四个资源条 HUD 元素）。

---

## 3. MiXianTu 侧行动清单（按顺序）

1. **依赖**：`build.gradle` 加 Saturn；exp4j 交给 Saturn。
2. **删包**：把 Saturn 已拥有的包从 MiXianTu 删除，全仓 import 改到 `com.iafenvoy.saturn.*`（机械，732/1361 个文件 import 过这些包）。
3. **`MxtAttachments`**：删 `RESOURCE_HOLDER` / `ABILITY_HOLDER`，改读 `SaturnAttachments.*`；其余附件留着。
4. **`MxtRegistries`**：删 23 个 key，保留 7 个（talisman/curse/aura_maximum/mount_render/formation_action/formation_stroke/secret_realm_generation）。
5. **`MxtResourceKeys`**：删 23 + 3 个 key，保留 33 个。
6. **`MxtDatapackRegistries`**：删 `resource` / `ability` / `trigger` / `incomplete` 四个注册；`IncompleteMarker` 与它的角标渲染一起删，角标改由 Saturn 画（贴图在 `assets/saturn/textures/gui/incomplete.png`）。
7. **内容 builtin 注册**：`DeferredRegister.create(SaturnRegistries.ENTITY_ACTION_TYPE, "mxt")`，条目 id 保持 `mxt:*`。
8. **aura**：`AuraAccess` / `AuraCostSource` 留在 MiXianTu；`AuraCost` 用 `context.get("mxt:aura_source")`；调用点改成 `context.with("mxt:aura_source", source)`。
9. **数据文件**：27 + 392 个 JSON 从 `data/*/mxt/` 移到 `data/*/saturn/`。
10. **lang**：Saturn 已有 81 条；MiXianTu 删掉被搬走的键（`hud.mxt.*`、HUD 编辑、性能分析那部分）；轮盘的 `key.mxt.wheel*` / `wheel.mxt.*` 全部保留。
11. **探针**：MiXianTu 的 GameTest import 改一遍；`verify/` 的腿仍测本体服务，不搬。
12. **文档**：`docs/` 注册表 id 与 `mxt/` 路径前缀改 `saturn/`；标掉被删字段。
13. **别名**：在 mod 构造里调用 `SaturnAliases.add("mxt")`；Saturn 在 LOWEST 优先级的 `RegisterEvent` 里把每个 `saturn:` 条目用 `mxt:` 再注册一次（值先包一层，避免 `Adding duplicate value`），所以既有数据包不必先改 id 就能解析。有改名过的条目先用 `SaturnAliases.set(custom)` 换规则。
14. **键位**：`MxtKeyMappings` 保留全部内容键位与轮盘五个键、12 个槽位键；只有 HUD 编辑键改成读 `SaturnKeyMappings.HUD_LAYOUT`。轮盘的状态、页面、格子、HUD 网格、贴图与菜单全部留在 MiXianTu，不向 Saturn 注册。
15. **profiler**：界面与 payload 留着，数据读 Saturn 的 `ServerProfiling.snapshot`；在 MiXianTu 构造里注册自己的 source 桶。
16. **指令**：Saturn 带走 `/saturn`（profiler / registries / attachment / ability list|cast|grant|revoke / triggers list|rules|publish / resource / 客户端 hud），并按 `config/saturn-common.toml` 的 `commands.*`（默认开）把同一批节点再注册到根。MiXianTu 的 `MxtCommand` 只剩内容附件段与 `/mxt wheel`；`AbilityCommand` 与 `TriggersCommand` 整体删除。
17. **KubeJS**：Saturn 的 `SaturnKubeJsPlugin` 提供 `SaturnActions` / `SaturnConditions` / `SaturnCosts` / `SaturnValues` / `SaturnResources`；MiXianTu 的插件删掉这五组绑定（继续用 `Mxt*` 全局名以免破坏脚本），或让脚本改用 `Saturn*`。

---

## 4. 注册表归属

### 4.1 代码注册表（Saturn，23 个）

`ability_type`(empty)、`ability_target_selector_type`(self)、`cost_type`(resource)、`data_storage_type`(empty)、`trigger_type`(use)、`number_provider_type`(constant)、`formula_function`(round)、`formula_variable`(zero)、`resource_value_provider_type`(current)、四个 `*_action_type`(no_op)、五个 `*_condition_type`(always)、`resource_bar_render_data_type`(missing)、`resource_bar_context`(self_hud)、`resource_bar_visibility_type`(always)、`item_matcher_entry_type`(item)、`timeline_entry_type`(idle)（`mount_render_type` 随御剑一起还给内容侧）。

### 4.2 数据包注册表（Saturn，4 个）

`saturn:resource`、`saturn:ability`、`saturn:trigger`、`saturn:incomplete`，文件在 `data/<命名空间>/saturn/<注册表>/`。

### 4.3 留在 MiXianTu 的

代码注册表 7 个；数据包注册表 39 个（`realm_stage`、`element`、`technique`、`artifact`、`pill`、`talisman`、`curse`、`aura`、`aura_zone`、`item_binding`…）。

---

## 5. 已注册的内置条目（`registry/content/*`）

| 注册表 | 已注册 id |
|---|---|
| `timeline_entry_type` | `action`、`branch`、`idle`、`wait_for` |
| `number_provider_type` | `constant`、`expression`、`context_variable`、`sum`、`uniform`、`binomial`、`weighted_list`、`conditional`、`js` |
| `formula_function` | `round`、`clamp`、`min`、`max` |
| `formula_variable` | `zero`、`random`、`caster`、`target` |
| `ability_type` | `active`、`triggered`、`modifier`、`aura`、`interval`、`channelled`、`targeted`、`empty`、`upkeep` |
| `ability_target_selector_type` | `self`、`area`、`ray`、`cone`、`js` |
| `data_storage_type` | `empty`、`cooldown`、`charges`、`toggle`、`timer`、`resource`、`target_lock`、`container`、`cast_deadline`、`channel_pulse`、`active_state`、`entry_began`、`idle_countdown`、`wait_countdown` |
| `item_matcher_entry_type` | `item`、`tag`、`wildcard`、`regex`、`ingredient` |
| `cost_type` | `resource`、`item`、`js` |
| `resource_value_provider_type` | `current`、`max`、`missing`、`constant`、`js` |
| `resource_bar_context` | `self_hud`、`target_overlay`、`boss_overlay` |
| `resource_bar_visibility_type` | `always`、`non_full`、`non_zero`、`recently_changed`、`resource_range`、`and`、`or`、`not` |
| `resource_bar_render_data_type` | `textured_bar`、`segmented_bar`、`radial_bar`、`text_only`、`boss_bar`、`missing` |
| `entity_action_type` | `no_op`、`sequence`、`chance`、`if_else`、`choice`、`send_message`、`js`、`dismount`、`extinguish`、`heal`、`add_resource`、`grant_ability`、`remove_ability`、`apply_effect`、`teleport`、`knockback`、`modify_ability_storage`、`spawn_entity`、`spawn_projectile`、`add_experience`、`add_velocity`、`exhaust`、`feed`、`gain_air`、`give_item`、`play_sound`、`remove_effect`、`set_fall_distance`、`set_no_gravity`、`set_on_fire`、`swing_hand`、`emit_game_event`、`passenger_action`、`block_action`、`self_bientity_action`、`equipped_item_action`、`riding_action`、`spawn_particles`、`spawn_effect_cloud`、`no_op`、`sequence`、`chance`、`if_else`、`choice`、`actor_action`、`target_action`、`send_message`、`js`、`mount`、`heal_target`、`transfer_resource`、`add_velocity`、`teleport` |
| `bi_entity_action_type` | `no_op`、`sequence`、`chance`、`if_else`、`choice`、`actor_action`、`target_action`、`send_message`、`js`、`mount`、`heal_target`、`transfer_resource`、`add_velocity`、`teleport` |
| `block_action_type` | `no_op`、`sequence`、`chance`、`if_else`、`choice`、`offset`、`send_message`、`js`、`set_block`、`break_block`、`schedule_tick`、`bonemeal`、`light_up`、`explode`、`spawn_entity` |
| `item_action_type` | `no_op`、`sequence`、`chance`、`if_else`、`choice`、`send_message`、`js`、`damage_item`、`consume_item`、`consume_health`、`cooldown`、`remove_enchantment`、`add_enchantment`、`merge_components` |
| `entity_condition_type` | `always`、`never`、`and`、`or`、`not`、`chance`、`js`、`sneaking`、`has_ability`、`partner`、`resource_compare`、`entity_tag`、`air`、`dimension`、`entity_type`、`fall_distance`、`glowing`、`health`、`exposed_to_sky`、`food_level`、`mob_effect`、`on_block`、`time_of_day`、`using_item`、`brightness`、`exposed_to_sun`、`experience_level`、`experience_points`、`relative_health`、`saturation_level`、`team`、`attribute`、`block_collision`、`can_have_effect`、`gamemode`、`passenger`、`attack_cooldown`、`equipped_item`、`main_hand_item`、`in_block`、`in_block_anywhere`、`scoreboard`、`riding`、`riding_recursive`、`passenger_recursive`、`riding_root`、`ability_storage_toggle`、`ability_storage_timer`、`ability_storage_resource`、`ability_storage_target`、`ability_storage_charges`、`ability_storage_cooldown`、`always`、`never`、`and`、`or`、`not`、`chance`、`actor_condition`、`target_condition`、`both`、`either`、`mutual`、`undirected`、`js`、`distance`、`team`、`relation`、`can_see`、`riding_recursive`、`same_team`、`relative_rotation` |
| `bi_entity_condition_type` | `always`、`never`、`and`、`or`、`not`、`chance`、`actor_condition`、`target_condition`、`both`、`either`、`mutual`、`undirected`、`js`、`distance`、`team`、`relation`、`can_see`、`riding_recursive`、`same_team`、`relative_rotation` |
| `block_condition_type` | `always`、`never`、`and`、`or`、`not`、`chance`、`offset`、`js`、`block_id`、`block_tag`、`biome_tag`、`hardness`、`height`、`light_level`、`slipperiness`、`blast_resistance`、`movement_blocking`、`adjacent` |
| `item_condition_type` | `always`、`never`、`and`、`or`、`not`、`chance`、`js`、`item_id`、`item_tag`、`item_matcher`、`amount`、`fuel`、`is_equipable`、`relative_durability`、`armor_value`、`durability`、`on_cooldown`、`ingredient`、`tool_ability`、`base_enchantment`、`has_component`、`component` |
| `damage_condition_type` | `always`、`never`、`and`、`or`、`not`、`chance`、`js`、`amount_range`、`directness`、`damage_type`、`damage_type_tag`、`fire`、`magic`、`projectile` |
| `trigger_type` |  + -9 个原版准则移植 + `js` |

不注册：`regen`（灵气）、`aura_pulse`（灵气）、`breakthrough` / `progression_level`（玩法信号）。

## 6. 验证

| 验证 | 结果 |
|---|---|
| Saturn `gradlew.bat compileJava` | 通过（547 文件 / 23 325 行） |
| Saturn GameTest | 无测试包，未跑 |
| 实机 / 数据包加载 | 未做 |
| HUD 编辑键位与 HUD 渲染、物品角标 | 只编译过，没在客户端看过 |
| MiXianTu 侧 | 未改动、未编译 |

---

## 7. 未决与待确认

1. **内容事件**：`event/` 里进了 7 个通用事件，17 个内容事件（修炼、诅咒、丹药、阵法、秘境…）没搬；要不要也抽成前置。trigger 的脚本绑定同样还没做。
2. **客户端指令的根合并**：客户端节点挂在 `/saturn` 下靠 Brigadier 同名合并，**未实机验证**；若被服务端树覆盖就改独立顶层 literal。
3. **aura 付款键名**：`CostContext.with("mxt:aura_source", …)` 由 MiXianTu 定义；是否登记"保留键"前缀规则。
4. **KubeJS 全局名**：Saturn 用 `Saturn*`，MiXianTu 用 `Mxt*`；是否统一。
5. **探针**：Saturn 没有 GameTest；要不要从 MiXianTu 搬一部分回归。
6. **版本与发布**：版本号、Maven 坐标、是否与 MiXianTu 同版本发布，尚未定。

---

## 8. 复制与改名是怎么做的（可复现）

1. 按 MiXianTu `research/audit/前置抽取评估.md` 的"框架候选"清单挑文件。
2. 每个文件替换：`com.iafenvoy.mxt` → `com.iafenvoy.saturn`；`\bMiXianTu\b` → `Saturn`；`\bMxt(?=[A-Z])` → `Saturn`；`"mxt:` → `"saturn:`；`"mxt.` → `"saturn.`；再补一遍字符串里的 `.mxt.` → `.saturn.`。
3. 文件名同样 `Mxt*` → `Saturn*`，写到 `src/main/java/com/iafenvoy/saturn/` 的同一相对路径。
4. 删 aura-only 文件、改写 §2.3 的文件、补齐 §2.4 的骨架；轮盘整块不复制。
5. `build.gradle` 加 `mavenCentral()`、KubeJS 的两个 maven、`exp4j` 的 `jarJar`、`compileOnly` KubeJS，然后 `gradlew.bat compileJava`。
