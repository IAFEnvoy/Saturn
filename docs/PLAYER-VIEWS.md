# 玩家信息视图与展示行

Saturn 根据**调用方传入的数据**生成只读玩家视图与信息行。它不管理玩家存档、不读取文件、不保存归档，也不自动发送玩家数据。在线实体、NBT、统计/进度文本和已有快照都由宿主提供；持久化、权限和传输由宿主负责。

## 基本使用

```java
import com.iafenvoy.saturn.data.information.PlayerViews;
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetters;

var view = PlayerViews.parse(player);
var health = view.read(EntityValueGetters.HEALTH);
var all = PlayerInformation.collectAll(view);
var basic = PlayerInformation.collect(view, PlayerInformation.BASIC);
```

`all.rows()` 是跨分组的完整有序行列表，`basic.rows()` 只包含指定分组。`all.rows(group)` 也能对已有文档筛选，`all.groups()` 取得文档中的分组。Group 的 rows 通过 Holder/Tag 引用独立 Row，同一行可被多个组选择，全取/多组并集只产生一份行。全取包含未被任何组引用的行，它们的 groups 为空。`row.groups()` 获取全部组，旧 `row.group()` 仅为兼容返回第一项，无组时不可调用。

分组类型为 `InfomationGroup`，主要用于 MXT 的分栏等前端布局，是展示元数据，不是取数前提。**全取和按分组取同时保留**。显式传入空的分组集合表示不取任何行，不代表全部。

```java
var selected = PlayerInformation.collect(view, InformationContext.DEFAULT,
        Set.of(PlayerInformation.BASIC, PlayerInformation.STATUS));
```

包结构如下，服务入口直接位于 `data.information`：

| 包 | 主要内容 |
| --- | --- |
| `com.iafenvoy.saturn.api.playerview` | `PlayerView`、行 provider、writer 与宿主查询契约 |
| `com.iafenvoy.saturn.data.information` | `PlayerViews`、`PlayerViewSnapshot`、`PlayerInformation`、`PlayerInformationRegistry`、`InformationText`，以及定义 `InformationRow` / `InformationGroup` |
| `com.iafenvoy.saturn.data.information.getter` | `EntityValueGetter`、内置常量 `EntityValueGetters`、参数身份 `EntityValueKey` |
| `com.iafenvoy.saturn.data.information.getter.builtin` | 26 个内置 getter 类型，各自实现实时采集、可选存档读取与快照投影 |
| `com.iafenvoy.saturn.registry.content` | `SaturnEntityValueGetters`、`SaturnInformationExpressions` 等内置条目注册 |
| `com.iafenvoy.saturn.data.information.expression` | 表达式接口、求值上下文与中间值 |
| `com.iafenvoy.saturn.data.information.expression.builtin` | 内置表达式，每个 Type 一个独立实现类 |
| `com.iafenvoy.saturn.data.information.model` | `InformationRowResult`、`InfomationGroup`、信息文档、快照元数据、查询结果等 DTO |
| `com.iafenvoy.saturn.screen.information` | 客户端默认信息页与 `PlayerInformationScreens` 入口 |

## 数据包定义与 Java 展示行

内置 16 条行已经迁移到 `saturn:information_row`，分组定义位于 `saturn:information_group`。`InformationRow` 是执行定义，`InformationRowResult` 是求值结果；`InformationGroup` 是组定义，`InfomationGroup` 是展示元数据。成员关系由 Group 的 `rows` 声明，其元素为 `Either<Holder<InformationRow>, TagKey<InformationRow>>`；Row 定义不含 groups。表达式 Type 注册在 `saturn:information_expression_type`，通过带参数 getter 读取数据并组合展示值。完整 JSON 参数、示例、Type 注册和兼容迁移见 [数据驱动信息行](INFORMATION-DATA.md)。

从 Player 解析或通过 RegistryOps 解码的快照保留其注册表上下文，可直接使用原 collect 入口。没有上下文的手工视图应显式传入 `new InformationContext("zh_cn", registries)`；否则只收集 Java 注册行。Group 和行定义不会缓存到全局世界状态。

以下为保留的 Java 扩展入口：

展示扩展统一注册到 Saturn，所有前端共享。可以使用 `register(id, order, importance, provider)` 独立登记行；以下旧带组重载作为兼容入口，同时把 Row ID 加入代码 Group 的默认选取范围，多组使用 `List.of(groupA, groupB)`：

```java
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.model.*;

InfomationGroup cultivation = PlayerInformation.registerGroup(
        Identifier.parse("mxt:cultivation"),
        Component.translatable("info.mxt.cultivation"), 100);

PlayerInformation.register(Identifier.parse("mxt:realm"), cultivation, 0, 100,
        (view, context) -> Optional.of(new InformationRowContent(
                Component.translatable("info.mxt.realm"),
                new InformationValue.Text(Component.literal("example")),
                Set.of())));
```

这是供扩展方参考的示例，示例翻译键由扩展方提供。实际 provider 应读取该模组注册的 getter，并在 `sourceFields` 中声明 getter 类型及依赖（可用 `EntityValueGetters.sources(getter)`），不能在回调里读当前玩家或重新执行玩法结算。

五参数注册签名为 `register(id, group, order, importance, provider)`。`importance` 为 int，数值越大越重要，允许负值；保留的四参数注册重载默认传 `0`。`PlayerInformationRegistry.register` 同样提供这两种重载。重要程度与布局顺序分别管理，默认收集仍包含所有启用且满足显示条件的行。多组行的顺序规则见数据驱动文档。

例如在内容较少的信息卡片中保留重要程度至少为 50 的行，完整页面仍使用原文档：

```java
var full = PlayerInformation.collectAll(view);
var compact = full.withMinimumImportance(50);
int importance = full.rows().getFirst().importance();
```

`withMinimumImportance` 包含等于阈值的行，保持原有展示顺序与来源字段，不修改原文档；也可在按分组收集后使用。定义和结果 Codec 的 `importance` 均默认为 `0`。结果构造器包括 `InformationRowResult(id, group, order, content)`、`InformationRowResult(id, group, order, importance, content)` 和多组版本。结果 Codec 写 `groups` 数组，读取兼容旧 `group` 对象；旧客户端不能读取新格式，联网两端应同步升级。扩展方自行选择重要程度，没有强制等级表。

内置行使用以下重要程度，行 ID 均属于 `saturn` 命名空间：

| importance | 信息行 | 用途 |
| --- | --- | --- |
| 100 | `name`、`health` | 最小身份与生命概览 |
| 80 | `experience` | 经验等级 |
| 70 | `abilities` | 能力概览 |
| 60 | `dimension`、`effects`、`equipment` | 常规状态与装备概览 |
| 50 | `food` | 饥饿值 |
| 40 | `game_mode`、`absorption`、`inventory` | 游戏模式、扩展状态与背包明细 |
| 30 | `resources` | 资源明细 |
| 20 | `air`、`attributes`、`ender_chest`、`carried_item` | 完整明细 |

例如阈值 `80` 展示姓名、生命与经验等级，阈值 `60` 再加入能力、维度、效果与装备，阈值 `50` 再加入饥饿值，阈值 `20` 包含全部内置行。降低阈值可增加内容；未显式设置重要程度的扩展行仍为 `0`，应使用阈值 `0` 或全取入口将其包含。重要程度不改变行的原有分组和显示顺序。

- 注册在模组初始化阶段完成。注册器支持同步登记，不在回调执行期间持有全局锁。
- 行 ID、order 和 importance 由行注册器持有，Group 持有行选取关系，provider 只返回内容。Java 注册 ID 重复会拒绝；数据包同 ID Row 定义在收集时覆盖 Java 行，禁用定义会屏蔽它；同 ID Group 定义替换代码组的默认行选取范围。
- 分组按 order、ID 排序，组内行按 order、ID 排序；全取的多组行以所属组排序最靠前者决定跨组顺序。`InfomationGroup` 的身份只取 ID，不受标题语言影响。
- `Optional.empty()` 表示隐藏；需要展示“未记录”时返回 `InformationValue.Missing`。
- 列表、表格和物品集合属于一条逻辑行的内容，可以由界面展开为多行。动态数据不修改全局注册表。
- 单行 provider 抛出运行时异常会记录并跳过该行，同组其他行继续生成。

内置分组常量均为 `InfomationGroup`：`BASIC`、`STATUS`、`EQUIPMENT`。第三方可向相应行标签添加数据行，或定义自己的组。纯 Java provider 不在原生 Row 注册表里，不能被行标签或 Holder 引用。

## Getter 与缺失值

`PlayerView.read(getter)` 返回 `ViewValue<T>`：

- `value()` 有值表示已知，零和空集合均是有效值。
- `missingReason()` 说明未记录、未请求、不支持、隐藏、无效或不适用。
- 已知值的 `origin()` 区分捕获值、保存值和根据当前规则重建的值。

快照在 `put` 时通过 getter 的 `valueCodec()` 编码，`read` 时独立解码，因此不会持有活玩家、可变附件或调用方集合。返回值即使可以修改，也不影响快照。`PlayerViewItem` 的 NBT 与 Component、展示模型的 Component 同样复制后返回。

标准字段覆盖身份、生命/吸收/氧气、食物、经验、位置、游戏模式、选中快捷栏、装备、背包、末影箱、鼠标物品、效果、属性、Saturn 资源和能力、个人分数及队伍。

`INVENTORY` 仅为普通背包槽，装备另在 `EQUIPMENT`；空槽从集合省略。保存数据中的装备以 MC 26 `equipment` 为准，主手从 `Inventory` 与 `SelectedItemSlot` 补出。效果持续时间和资源边界均为输入时的数据，不随查看时间继续结算。

Saturn 不为读取界面创建附件，也不初始化资源。保存的属性仅能确认基值时，有效值保持缺失；不会根据当前规则猜出历史最大健康。保存的物品保留原始数据和 ID，即使原物品模组已删除也能显示 ID；真正构造渲染用物品时才调用 `PlayerViewItem.stack(registries)`，失败返回空。

## 参数化查询

内置 22 个无参 getter 保留原有身份、状态、背包、属性列表等事实；另有 4 个带参类型按稳定 ID 选择列表成员，无需依赖数组顺序：

| 类型 | 必填参数 | 可选参数 | 结果 |
| --- | --- | --- | --- |
| `saturn:attribute` | `attribute`：属性 ID | `value`：`base` / `effective`，默认 `effective` | double |
| `saturn:resource` | `resource`：资源 ID | `value`：`current` / `minimum` / `maximum`，默认 `current` | double |
| `saturn:effect` | `effect`：效果 ID | `value`：`amplifier` / `duration`，默认 `amplifier` | int |
| `saturn:score` | `objective`：目标名称 | 无 | int |

```java
import com.iafenvoy.saturn.data.information.getter.builtin.AttributeGetter;

var query = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.BASE);
var base = view.read(query);
// 显式要求归档一条查询，meta 由宿主提供：
var captured = PlayerViews.parse(player, meta, List.of(query));
```

缺少确切查询结果时，上述内置 getter 可以通过 `fallback(view)` 从已保存的宽列表投影；不会寻找活实体。确切结果已存在（包括明确 missing、无效载荷或未知 schema）时，不允许投影覆盖。已知列表中没有该成员为 `NOT_APPLICABLE`；历史属性有效值或资源边界未记录为 `NOT_CAPTURED`，不按当前规则猜值。

## 注册类型化扩展

注册表为 `saturn:entity_value_getter`，入口是 `SaturnRegistries.ENTITY_VALUE_GETTER` / `SaturnResourceKeys.ENTITY_VALUE_GETTER`，条目类型为 `MapCodec<? extends EntityValueGetter<?>>`。参数与结果分开编码，例如一个按倍率读取生命值的查询：

```java
public record ScaledHealthGetter(double scale) implements EntityValueGetter<Double> {
    public static final MapCodec<ScaledHealthGetter> CODEC = MiscCodecs.FINITE.fieldOf("scale")
            .xmap(ScaledHealthGetter::new, ScaledHealthGetter::scale);

    @Override public MapCodec<ScaledHealthGetter> codec() { return CODEC; }
    @Override public Codec<Double> valueCodec() { return MiscCodecs.FINITE; }

    @Override public ViewValue<Double> capture(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return ViewValue.missing(MissingReason.UNSUPPORTED);
        return ViewValue.known(living.getHealth() * scale, ValueOrigin.CAPTURED);
    }

    @Override public ViewValue<Double> readSaved(SavedPlayerViewContext context) {
        var health = context.player().getFloat("Health");
        if (health.isEmpty()) return ViewValue.missing(MissingReason.NOT_CAPTURED);
        return ViewValue.known(health.orElseThrow() * scale, ValueOrigin.SAVED);
    }
}

public static final DeferredRegister<MapCodec<? extends EntityValueGetter<?>>> GETTERS =
        DeferredRegister.create(SaturnRegistries.ENTITY_VALUE_GETTER, "example");
static {
    GETTERS.register("scaled_health", () -> ScaledHealthGetter.CODEC);
}
// 模组构造时：GETTERS.register(modEventBus);
```

对应配置是 `{"type":"example:scaled_health","scale":2}`。`codec()` 必须返回注册的参数 Codec；`type()` 从注册表查出 ID。`valueCodec()` 只编解码查询结果，`schemaVersion()` 默认 `1` 且必须为正整数。`capture` 默认 `UNSUPPORTED`，`readSaved` / `fallback` 默认 `NOT_CAPTURED`；`dependencies()` 默认空列表，可声明快照投影需要的宽查询。回调只返回自己的 `ViewValue<T>`，不接收跨查询 writer。

`PlayerViews.parse` 合并三类查询：参数 Codec 能从空对象解码的默认查询、调用方显式请求、当前世界全部启用信息行引用的查询；同时加入 dependencies，并按完整参数 JSON 去重。必填参数类型不会自动枚举全部参数，需由行或调用方请求；Java provider 需要参数查询时也应显式传入。`EntityValueGetters` 提供内置常量、`defaults`、`requireRegistered` 和来源计算 `sources`，不持有字段 Map。

单查询回调抛异常、返回 null 或结果 Codec 编码失败，仅标该查询为 `INVALID`。保存数据返回 `NOT_CAPTURED` 时不创建封套，宿主可用 `builder.contains(getter)` 判断并补入信息；其他明确 missing 会保存。

注册遵循 NeoForge 生命周期，冻结后不能增删类型。未知 getter 类型在定义 Codec 中报错；快照原始查询仍可保留。getter 不参与 Saturn 旧命名空间别名复制。新解析行为需要 Java 注册参数 Codec 和实现，数据包只能选择类型与参数。

服务端实体必须在服务端主线程解析，客户端实体由调用方保证线程。getter 回调不能加点、创建附件、初始化资源或推进玩法；存档回调只读传入数据，fallback 只访问传入视图。

## 解析调用方提供的保存数据

```java
var context = new SavedPlayerViewContext(meta, registries, playerNbt,
        Optional.of(statsJson), Optional.of(advancementsJson));
var view = PlayerViews.parse(context);
var supplemented = PlayerViews.parse(context, Optional.of(recordedView));
```

`meta` 由调用方提供，包括账号、来源、记录 ID、快照 ID、真实捕获时间（可空）及本次组装时间。前世应使用不同的记录 ID，不能仅以 UUID 区分缓存。

传入的 recordedView 优先，存档读取只补完全没有保存的确切查询，且必须与 context 引用同一记录。查询缺少与载荷损坏分别表示 `NOT_CAPTURED` 和 `INVALID`；单查询损坏不会阻断其他查询。带参数请求可传给 `parse(context, Optional.of(recordedView), requested)`。

原版玩家存档不包含完整 GameProfile；需要姓名、皮肤属性、单独保存的鼠标物品、计分板等内容时，调用方可先向 builder 放入已知字段，再交给解析器补缺项。统计与进度可通过 context 的独立 JSON 文本传入。实体解析不会访问原版私有统计/进度存储，也不会调用保存或创建进度的方法，这两项在该路径标为 `UNSUPPORTED`。

输入 NBT 应已由调用方完成必要的原版/模组版本迁移；内置读取器按 MC 26 当前结构解析，不自行定位文件或调用 DataFixer。传入摘要就只显示摘要中能确认的事实。

## Codec 与宿主集成

`PlayerViewSnapshot.CODEC` 提供 NBT/JSON 编解码，调用方需要注册表上下文时使用 `registries.createSerializationContext(ops)`。Codec 不执行磁盘读写。

封套独立携带 schema、known/missing 状态、来源及载荷。顶层版本保持 `1`：无参 `{type}` 查询仍放在旧 `fields`，内置 ID/schema/载荷兼容；带参数结果放在可选 `queries` 数组，每项包含完整 `getter` 定义与 `data` 封套。不同参数不会覆盖。未知字段封套、参数查询定义及载荷均保留，未安装其模组也能往返；schema 不支持或载荷无效只影响该查询。固定顶层未知键按普通 Codec 规则忽略，不承诺保留。`getterIds()` 枚举两种存储中的类型 ID。

`snapshot.filter(allowedGetterType)` 同时过滤 fields / queries，包括未知扩展；信息文档 `filter` 按 `sourceFields` 过滤整行。该历史命名现在表示 getter 类型及依赖 ID，例如 attribute 同时声明 `saturn:attribute` 与 `saturn:attributes`。过滤为类型粒度，不是按某个属性参数授权；有更细需求时宿主应自行投影和限制。宿主基于实际连接授权，不能信任客户端自报身份。

`PlayerViewRequest`、`PlayerViewResult` 与 `PlayerViewAccess` 是可选的宿主查询契约。Saturn 没有全局网络安装器，也不自行查询账号/归档。请求 ID 供宿主丢弃过期响应；调用方明确列出需要的字段，空列表不隐式授予全部数据。

## 默认界面与接入边界

仅客户端调用：

```java
minecraft.setScreen(PlayerInformationScreens.create(parent, view));
minecraft.setScreen(PlayerInformationScreens.create(parent, view, group));
```

默认页面显示传入快照的通用文字行、列表、物品槽位文本和表格，支持滚动和 tooltip。`InformationText` 提供同一套文字投影，AUI/卡片也可以直接渲染类型化 `InformationValue`。

默认页面按 Group 展示，未分组行进入“其他信息”，多组行分别出现在引用它的组中；按组创建页面只展示指定组。该页面不自动刷新、不请求数据、不提供写操作。人物三维外观预览、加载控制和网络刷新由后续客户端适配完成；当前页面不会按历史 UUID 借用今世实体。MXT、System、Reincarnation 的业务迁移也不包含在本次 Saturn 基础模块中。

## 文字查询指令

```mcfunction
/saturn information @s
/saturn information PlayerName saturn:basic
/saturn information @e[type=minecraft:cow,limit=1] saturn:status
```

`/saturn information <target> [<group>]` 要求 gamemaster 权限。target 为玩家名、UUID 或单实体选择器；省略 group 全取，提供 group 按 ID 筛选，组参数补全当前世界的数据定义和 Java 注册组。非玩家仅能得到其支持的数据，其余按原有缺失策略展示。指令现场解析实体，使用同一 `PlayerInformation` / `InformationText` 路径输出标题、文字行和表格；只回复执行者，返回逻辑行数。未知组给出明确错误，空组给出“暂无信息”。提示和内置行均使用翻译键，客户端按自身语言显示。

## 旧 API 与数据迁移

`PlayerViewField` / `PlayerViewFields`、独立 Contributor / SavedReader 已移除。将每个字段迁成 `EntityValueGetter`，注册参数 MapCodec，将旧结果 Codec 改为 `valueCodec()`；无参可用 `MapCodec.unit(INSTANCE)`。将 `capture(Player)` 改成 `capture(Entity)` 并判断所需实体类型。旧 `view.*` 导入迁到上表的 `data.information.*`；结果 `InformationRow` 改名 `InformationRowResult`。

JSON 的 `{"type":"saturn:field","field":"saturn:health"}` 改成 `{"type":"saturn:getter","getter":{"type":"saturn:health"}}`，可选 path 保留。旧基本快照只要 ID/schema/结果 Codec 一致就能继续读取；参数结果是新扩展，旧版 Codec 会忽略 queries，无法使用或保留它们。新旧网络两端应升级到相同 API/数据格式；本地改动尚未替换宿主的已发布依赖。

## 验证

```powershell
.\gradlew.bat compileJava verifyPlayerViews runGameTestServer build --console=plain
```

`verifyPlayerViews` 位于独立的 `src/verification/java` 源码集，不进入发布 JAR，不启动游戏、不构造 Player、不创建世界，也不读写玩家存档。独立 fixture 填充真实 getter 注册表，覆盖分组/全取、顺序、故障隔离、快照复制、NBT/JSON 往返、未知扩展、schema、异常/null/无效载荷隔离、宿主补充、明确 missing 优先级、参数身份、快照投影、求值缓存和重要程度/来源过滤。

独立检查使用无需游戏 bootstrap 的数据和数字展示模型。main 源码集 `gametest.InformationTests` / `EntityValueGetterTests` / `InformationCommandTests` 进一步验证原生注册及冻结、带参扩展、实时采集、存档读取、不创建附件、快照脱离可变物品，以及数据包、表达式、Holder/Tag 与文档 Codec。指令测试实际执行全取、分组、补全、错误、权限和非玩家查询，并核对每一行文字。测试及专用 getter 仅在启用 GameTest 时注册；不使用 test-mod，客户端视觉效果仍需人工验收。
