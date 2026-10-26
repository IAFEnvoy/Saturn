# Saturn 对外契约

> 给依赖 Saturn 的内容模组看的：能加什么、怎么加、哪些东西 Saturn 故意不认识。
> 形状以代码为准；本文件与代码不一致时，先改本文件。

---

## 0. 依赖与安装

- NeoForge 26.1.2（`neo_version=26.1.2.99`），Java 25。
- exp4j（公式求值）已 `jarJar` 进 Saturn 自己的 jar，依赖方不必声明。
- KubeJS 是 **`compileOnly`**：`compat.kubejs` 的类只在装了 KubeJS 时被加载，没装不影响启动。
- 依赖 Saturn 的模组不需要在 `neoforge.mods.toml` 里声明可选依赖；Saturn 是硬依赖。

---

## 1. 命名空间与数据文件

- 模组 id 与注册表命名空间都是 `saturn`。
- 定义写在 `data/<你的命名空间>/saturn/<注册表路径>/<条目>.json`。
- **目录跟注册表 key 走，条目 id 跟你的命名空间走**：你可以把 `mymod:my_action` 注册到 `saturn:entity_action_type`，文件是 `data/mymod/saturn/entity_action_type/my_action.json`。
- 省略 `type` 字段时用注册表的默认条目（§2 表第二列），它们都在 `saturn` 命名空间下。

---

## 2. 注册表

### 2.1 代码注册表（`SaturnRegistries`）

| 静态字段 | 默认条目 |
|---|---|
| `ABILITY_TYPE` | `empty` |
| `ABILITY_TARGET_SELECTOR_TYPE` | `self` |
| `COST_TYPE` | `resource` |
| `DATA_STORAGE_TYPE` | `empty` |
| `TRIGGER_TYPE` | `use` |
| `NUMBER_PROVIDER_TYPE` | `constant` |
| `FORMULA_FUNCTION` | `round` |
| `FORMULA_VARIABLE` | `zero` |
| `RESOURCE_VALUE_PROVIDER_TYPE` | `current` |
| `ENTITY_ACTION_TYPE` / `BI_ENTITY_ACTION_TYPE` / `BLOCK_ACTION_TYPE` / `ITEM_ACTION_TYPE` | `no_op` |
| `ENTITY_CONDITION_TYPE` / `BI_ENTITY_CONDITION_TYPE` / `BLOCK_CONDITION_TYPE` / `ITEM_CONDITION_TYPE` / `DAMAGE_CONDITION_TYPE` | `always` |
| `RESOURCE_BAR_RENDER_DATA_TYPE` | `missing` |
| `RESOURCE_BAR_CONTEXT` | `self_hud` |
| `RESOURCE_BAR_VISIBILITY_TYPE` | `always` |
| `ITEM_MATCHER_ENTRY_TYPE` | `item` |
| `TIMELINE_ENTRY_TYPE` | `idle` |

```java
public static final DeferredRegister<MapCodec<? extends EntityAction>> ACTIONS =
        DeferredRegister.create(SaturnRegistries.ENTITY_ACTION_TYPE, MyMod.MODID);
```

### 2.1.1 内置条目（`registry/content`）

每个代码注册表一个类（SaturnEntityActions、SaturnEntityConditions、SaturnTriggers…），每个类持有自己的 DeferredRegister 并在静态块里注册；`registry/SaturnContent` 只是汇总表，由主类在构造时统一 .register(bus)。自己的条目另建一个类和本模组的 DeferredRegister 即可。

### 2.2 数据包注册表（`SaturnDatapackRegistries`）

`saturn:resource`、`saturn:ability`、`saturn:trigger`、`saturn:incomplete` 由 Saturn 注册，你只写 JSON。

- `saturn:incomplete`（`data/item/IncompleteMarker`）：把还没做完的物品标出来。文件里只有一个 `items`，装的是 `ItemMatcher` 的条目（物品、标签、通配、正则、`ingredient`，还能用 `type` 走自己的 `item_matcher_entry_type`）；任何一条命中就算命中，因此重叠不用排序——所有命中的声明画的是同一个角标。空 `items` 在加载期报错：那只能是写到一半的文件。
- 角标由 `render/item/IncompleteMarkerRenderer` 画，贴图 `assets/saturn/textures/gui/incomplete.png`；它在客户端为每个物品注册一个 `IItemDecorator`，所以标记作用于“堆叠”而不是“物品类”，标签与需要读堆叠数据的匹配也能生效。

---

## 3. 扩展点

### 3.1 支付：`Cost` + `CostContext`

Saturn 只实现 `saturn:resource` 与 `saturn:item`；其它通道自己实现：

```java
public record MyAuraCost(NumberProvider amount) implements Cost {
    public static final MapCodec<MyAuraCost> CODEC = ...;

    @Override
    public ResultWithData<Transaction> prepare(CostContext context) {
        AuraCostSource source = context.get("mymod:aura_source").map(AuraCostSource.class::cast).orElse(null);
        if (source == null) return ResultWithData.failure(null, CostFailure.NO_CHANNEL);
        return ResultWithData.success(source.bind(context, this.amount().evaluate(context.formula())));
    }

    @Override
    public MapCodec<MyAuraCost> codec() { return CODEC; }
}
```

- `CostContext` 是 `record(payer, formula, origin, account, itemTarget, data)`；`data` 是 `with(key, value)` / `get(key)` 的扩展袋子，Saturn 只搬运不解释，键一律带命名空间。
- `CostTarget` 是**开放接口**，你的 `Transaction` 可以带自己的目标类型。
- `CostPayment.pay(List<Cost>, CostContext)`：先全部 `prepare`，再全部 `execute(true)` 试算，最后按序 `execute(false)` 实扣；实扣失败记日志并继续。`PaymentResult` 带 `debits()` 与 `resources()`。

### 3.2 失败

失败是值不是异常。`SimpleFailure.of("my_reason")` 造常量；想有自己的 lang 命名空间就包进 `FailureSource.of(你的注册表 key)`。返回用 `Result` / `ResultWithData`。

### 3.3 附件

Saturn 只有两个实体附件：`SaturnAttachments.RESOURCE_HOLDER`（数值资源）与 `SaturnAttachments.ABILITY_HOLDER`（能力授予与状态）。自己的状态用你自己的附件；想按 tick 同步脏数据，继承 `util.AbstractChangeTrackable` 并参考 `SaturnAttachments.flushDirtyAttachments`。

### 3.4 数值资源

- 定义：`saturn:resource`（`default_value` / `min` / `max` / `icon` / `particle_color` / `bars`）。
- 读写：`entity.getData(SaturnAttachments.RESOURCE_HOLDER)` 的 `get` / `set`；批量扣取用 `ResourceTransactions.tryConsume`（全有或全无）。
- `FormulaContext.withResource(resource)` 让该资源的公式读到自己的数值；`ResourceService.formulaContext(...)` 是现成包装。

### 3.5 公式

- `NumberProvider` 内置 8 种（`constant` / `expression` / `context_variable` / `sum` / `uniform` / `binomial` / `weighted_list` / `conditional`），另有 `js`。
- 自己的变量注册到 `FORMULA_VARIABLE`，函数注册到 `FORMULA_FUNCTION`（exp4j `Function`）。
- `FormulaContext` 字段：`caster` / `target` / `player` / `registries` / `resource` / `random` + 显式值表。

### 3.6 HUD 与资源条

- 加 HUD：实现 `HudEntry`（或继承 `AbstractHudEntry`），客户端初始化时 `HudManager.register(entry)`；位置由 `HudAnchor`（8 锚点）与 `HudPlacement` 决定，玩家在 `HudEditScreen` 里拖；布局落在 `config/saturn/hud.json`。
- 资源条只在 `saturn:resource` 的 `bars` 里声明；外观由 `resource_bar_context` / `resource_bar_render_data_type` / `resource_bar_visibility_type` 三个注册表选。
- 四个资源条 HUD 元素（左/右两列、目标、Boss）由 SaturnClient 在客户端初始化时注册（ResourceBarOverlay.registerEntries()，幂等）；内容侧不需要再调。
- 物品角标：`render/item/IncompleteMarkerRenderer` 是 Saturn 注册的唯一 `IItemDecorator`，读 `saturn:incomplete`（§2.2）。
- 客户端两个值（显示名字、图标位置）在 `SaturnClientConfig`。

### 3.7 轮盘（不在 Saturn）

整块留在内容侧：键位、选择状态、页面与格子类型、HUD 网格、`/saturn wheel` 指令全在 MiXianTu。Saturn 只保留通用的 HUD 框架（§3.6）、键位持有类 `SaturnKeyMappings.KeyMappingHolder` 与技能激活入口（§3.11）。

### 3.8 指令（不含界面、不含网络）

根节点 `/saturn`，服务端与客户端节点都挂在下面（Brigadier 同名合并）：

| 节点 | 侧 | 作用 |
|---|---|---|
| `debug on` / `off` / `status [sources…]` | 服务端 | 性能分析开关与读数（管理员） |
| `registries` | 服务端 | 4 个数据包注册表大小 + 23 个代码注册表的「大小 / 默认条目」 |
| `attachment status` | 服务端 | 两个 Saturn 附件：资源项数、能力来源数、冷却数 |
| `ability list [target]` | 服务端 | 这个身体持有的技能与来源 |
| `ability cast <id>` / `grant <targets> <id>` / `revoke <targets> <id>` | 服务端 | 施放、按命令自己的来源授予与收回（管理员） |
| `triggers list [entity]` / `triggers rules <signal>` | 服务端 | 正在监听的订阅、某信号命中的规则 |
| `triggers publish <signal> [entity]` | 服务端 | 手动发布一个信号（管理员） |
| `resource get <id>` / `list` | 服务端 | 读执行者的资源数值 |
| `resource set/add <targets> <id> <amount>` | 服务端 | 写数值（管理员），按 `min`/`max` 夹取 |
| `hud` / `hud open` / `hud <element> reset` | 客户端 | HUD 框架诊断与编辑器 |

技能与触发器的节点都在：运行时已经搬进 Saturn（§3.11）。

### 3.9 缓存

`RegistryCache` 子类构造即自注册；`TagsUpdatedEvent`（`shouldUpdateStaticData`）重建，客户端登出清空。唯一的坑：类必须被初始化过——在**你的** mod 构造里持有一次你的缓存类的静态字段。Saturn 只 `init()` 自己的 `RegistryCaches`。

### 3.10 KubeJS（可选）

`compileOnly`；入口是 `kubejs.plugins.txt` 里的 `SaturnKubeJsPlugin`。

| 脚本全局 | 作用 |
|---|---|
| `SaturnActions` | 脚本构造四种 action（`type: "js"`） |
| `SaturnConditions` | 同上，五种 condition |
| `SaturnCosts` | 脚本构造 cost；`SaturnKubeJsApi.tryConsumeResources` 直接结一笔账 |
| `SaturnValues` | 脚本构造 `resource_value_provider`（`type: "js"`） |
| `SaturnResources` | 读写一个实体的 `saturn:resource` 数值 |

- 回调表：`SaturnJsActionCallbacks` / `ConditionCallbacks` / `CostCallbacks` / `ValueCallbacks` / `SelectorCallbacks` / `TriggerMatchers`；脚本重载时 `SaturnJsCallbacks.clear()`。
- **没有** trigger 的绑定（发布信号、读订阅属依赖方的运行时）。
- 全局名是 `Saturn*`，与内容模组的 `Mxt*` 不撞名。

### 3.11 技能与触发器运行时

Saturn 自带完整的技能与触发器运行时，内容侧只提供规则与内容。

**技能**（`runtime.ability`）：`AbilityService`（施放/蓄力/通道/按一下的整条流程）、`AbilityActivationService`（客户端按键与脚本的激活入口）、`AbilityGrantService`（按来源授予与收回）、`AbilityModifierService`（被动的属性修正）、`PassiveAttributeService`（属性来源表）、`AbilityStorage`（每技能状态）、`AbilityFeedback`、`AbilityEventBridge`（装备/物品/触发器的接线）。

**内置技能类型**：`active`、`triggered`、`modifier`、`aura`、`interval`、`channelled`、`targeted`、`empty`、`upkeep`。自己的类型注册到 `ABILITY_TYPE`，实现 `AbilityType`；要跑动作的再加 `AbilityEffect` / `ActionCarrier`，要长按的加 `Togglable`，要读时钟的加 `CooldownSource`，要通道的加 `ChannelSource`。

**触发器**（`runtime.trigger`）：`TriggerDispatcher`（订阅表 + 发布）、`TriggerRuleService`（`saturn:trigger` 规则：条件、概率、冷却）、`TriggerPublishing`、`TriggerSubscription`、`TriggerRehydrators`、`TriggerLifecycleEvents`，以及原版准则的两个桥（`VanillaTriggerBridge` / `VanillaTriggerPollBridge`）。规则的冷却记在 `SaturnAttachments.TRIGGER_COOLDOWNS`。

**钩子**：`api.AbilityHooks` 是 Saturn 向世界提问的唯一入口——伤害倍率、技能校验、施放提交时的准则、物品能否使用、物品给的技能、身体装备（Curios）。默认全为“不改变”，用 `AbilityHooks.set(...)` 在 mod 构造里装一份。

**事件**（`event`）：`AbilityUseEvent.Pre/Post`、`AbilityTriggeredEvent`、`ResourceConsumeEvent.Pre/Post`、`CollectAbilityGrantsEvent`、`CollectPassiveAttributesEvent`、`CollectTickStagesEvent`、`TriggerRehydrateEvent`。内容事件（修炼、诅咒、丹药、阵法、秘境…）不在 Saturn。

**来源与 tick 顺序**：`runtime.Sources` 生成 `saturn:grant/...`、`saturn:equipment/...` 这类来源 id，`Grant` / `Declaration` 是 record，内容可以造自己的类别；`runtime.EntityTickStages` 是每实体 tick 的阶段表（`RESOURCES` / `TRIGGERS` / `ATTRIBUTES` / `ABILITIES` / `CASTS` / `CHANNEL` …），内容用 `CollectTickStagesEvent` 贡献自己的阶段。

**内容侧要自己做的**：`word` / `storage` / `flight_control` 三种技能类型、`CultivationTriggerService`（修炼进度发信号）、内容事件与其 KubeJS 绑定。

### 3.12 通用工具与战利品条目

- `util/dispatcher`：`TickDispatcher` 按 key 排一次性或周期任务（`TickInterval` 定间隔、`TickExecutionLimit` 定每 tick 的执行上限）；纯 Java、归属创建线程，可 `reschedule` / `cancel` / `advanceTo`。
- `util/{CloseableResources,ClientRenderResources}`：可关闭资源的槽位（关卡卸载、停机时释放）；`ClientLevelAccess` 是客户端注册表的访问口。
- `util/InventoryUtil`：精确堆叠的原子挪进/挪出；`util/TooltipText`：tooltip 的数值、正负号与分隔符（`tooltip.saturn.separator`）。
- `data/DescribedEntry`：`值 + 可选 description` 的 codec 包装（`codec(valueCodec, key)`），任意 `T` 可用。
- `util/{PlayerNames,ClientPlayerNames}`：按 UUID 解析名字——先问服务端已知的（在线列表、名字缓存），客户端再问当前连接；客户端要问服务端时走 `api/PlayerNameQuery`（Saturn 没有自己的网络包）。
- 战利品条目：`saturn:has_ability`（条件：这个实体持不持有某个技能）与 `saturn:grant_ability`（函数：授予，默认来源 `saturn:loot`），只读 `saturn:ability` 与能力附件。
---

### 3.13 长按手势与按 id 的准则

- `api/ItemUseCycle` + `event/ResolveItemUseCycleEvent`：**通用长按绑定**。一次右键点击后，`ItemUseCycles` 在 `RightClickItem` 上发一次 `ResolveItemUseCycleEvent`，认领的提供者给出一段手势（`id` / `durationTicks` / `animation` / `canStart` / `tick` / `finish` / `isConsumption`）；`Item` 的 `use` / `getUseDuration` / `getUseAnimation` / `finishUsingItem` 由 `saturn.mixins.json` 里的 `ItemUseCycleMixin` 接过来。原版使用组件（`consumable`、`blocks_attacks`、`kinetic_weapon`、可穿戴）优先，原生 `PASS` 之外的结果、原生替换了堆叠、原生给了时长，都会让框架让路。手势状态按实体与堆叠弱引用保存，客户端通过原版同步的使用标记自行发现。
- `advancement/IdentifierCriterionTrigger`：一个准则实现、按 id 判断是否算数（`definition` 可选，缺省匹配所有调用）。内容侧用 `DeferredRegister.create(Registries.TRIGGER_TYPES, "<你的命名空间>")` 为每个用途注册一个实例，再在对应时刻 `trigger(player, definitionId)`。

### 3.14 别名（`AliasManager`）

搬过来的内置条目现在都叫 `saturn:<path>`，旧数据包里的 `mxt:<path>` 需要继续能解析：

```java
public MyMod(IEventBus bus) {
    SaturnAliases.add("mxt");                 // mxt:<path> 成为 saturn:<path> 的第二个 id
    // 有改名过的条目时先换规则：
    SaturnAliases.set((namespace, id) -> Optional.of(...));
}
```

- `api/AliasManager`：`Optional<Identifier> alias(String namespace, Identifier id)`，返回某个内置条目的旧 id，空表示这个命名空间没这么用过；`AliasManager.SAME_PATH` 把同样的 path 放进内容方命名空间。
- `registry/SaturnAliases`：`set(manager)` / `add(namespace)`。在 **`EventPriority.LOWEST` 的 `RegisterEvent`** 里，遍历 23 个内置注册器加 2 个原版战利品注册器的每个条目，用旧 id **再注册一次同样的值**——这时各注册器自己的条目已经注册、holder 已经绑定，注册表也还没冻结。
- 为什么要包一层：注册表对“同一个对象挂在两个 id 下”会抛 `Adding duplicate value`（`MappedRegistry` 用 `IdentityHashMap` 查重）。所以 `MapCodec` 用 `xmap(identity, identity)` 重造一个等价实例，`Function` 用 `SimpleFunction` 委托，`FormulaVariable` 与 `ResourceBarContext` 用记录式委托。
- 跳过：注册表已经占用的旧 id（内容侧自己还在用）、`saturn` 自己的命名空间、附件——附件 id 存在实体上，第二个 id 会变成第二份附件而不是改名。
- 时机：`add` 必须在 mod 构造里调用；setup 之后再加会打一条 warn 并忽略。
- 与 NeoForge `DeferredRegister.addAlias` 的区别：那套只把别名记进 `IRegistryExtension` 并随注册表快照同步，当前版本的原版查找（`Registry#get` / `byNameCodec`）不经过 `resolve`；真正让旧 id 可解析的是这里的第二次注册。

## 4. Saturn 这边还没做（依赖方不要当作已有能力）

| 缺什么 | 影响 |
|---|---|
| 轮盘 | 键位、选择状态、页面与格子类型、HUD 网格、`/saturn wheel` 都在内容侧；Saturn 只给 HUD 框架与 `AbilityActivationService`。 |
| 内容相关的技能类型 | `word`（解毒/自愈）、`storage`（法器储物）、`flight_control`（御剑）三种类型与 `CultivationTriggerService` 依赖内容运行时，留在内容侧。 |
| trigger 的脚本绑定 | `js` 的 trigger 类型在 Saturn，但把订阅/发布挂到脚本的绑定在内容侧。 |
| 灵气 / 池 / 银行 | 不在 Saturn；`Cost` + `CostTarget` + `CostContext.with(...)` 是你的通道。 |
| 品质（quality） | `quality` 匹配条目与 `QualityRequirement` 都不在 Saturn。 |
| 物品侧存储 | `DataStorageHolder` 只挂在 `AbilityAttachment` 上，没有物品组件。 |
| profiler 界面与网络 | 只有 `/saturn debug`；要图形界面自己读 `ServerProfiling.snapshot`。 |
| 测试 | 没有 GameTest。 |
| 内置数据包定义 | Saturn 不含任何 JSON。 |

---

## 5. 版式约定

- 公共 API 都在 `com.iafenvoy.saturn` 下：`data.*` 数据模型、`registry.*` 注册表、`runtime.*` 运行时、`screen.*` 客户端、`compat.kubejs` 可选脚本桥。
- `data.*` 的 record 字段名就是 JSON 字段名，改字段等于破坏数据包。
- 内部类（`builtin/*`、`util/profiling` 的细节）不承诺兼容。
