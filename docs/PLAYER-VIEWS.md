# 玩家信息视图与展示行

Saturn 根据**调用方传入的数据**生成只读玩家视图与信息行。它不管理玩家存档、不读取文件、不保存归档，也不自动发送玩家数据。在线实体、NBT、统计/进度文本和已有快照都由宿主提供；持久化、权限和传输由宿主负责。

## 基本使用

```java
import com.iafenvoy.saturn.view.PlayerViews;
import com.iafenvoy.saturn.view.PlayerInformation;
import com.iafenvoy.saturn.view.PlayerViewFields;

var view = PlayerViews.parse(player);
var health = view.read(PlayerViewFields.HEALTH);
var all = PlayerInformation.collectAll(view);
var basic = PlayerInformation.collect(view, PlayerInformation.BASIC);
```

`all.rows()` 是跨分组的完整有序行列表，`basic.rows()` 只包含指定分组。`all.rows(group)` 也能对已有文档筛选，`all.groups()` 取得文档中的分组。

分组类型为 `InfomationGroup`，主要用于 MXT 的分栏等前端布局，是展示元数据，不是取数前提。**全取和按分组取同时保留**。显式传入空的分组集合表示不取任何行，不代表全部。

```java
var selected = PlayerInformation.collect(view, InformationContext.DEFAULT,
        Set.of(PlayerInformation.BASIC, PlayerInformation.STATUS));
```

包结构如下，服务入口直接位于 `view`：

| 包 | 主要内容 |
| --- | --- |
| `com.iafenvoy.saturn.api.playerview` | `PlayerView`、解析扩展、行 provider、writer 与查询契约 |
| `com.iafenvoy.saturn.view` | `PlayerViews`、`PlayerViewFields`、`PlayerViewSnapshot`、`PlayerInformation`、`PlayerInformationRegistry`、`InformationText` |
| `com.iafenvoy.saturn.view.model` | `InfomationGroup`、信息行/文档、快照元数据、字段值及其他 DTO |
| `com.iafenvoy.saturn.screen.information` | 客户端默认信息页与 `PlayerInformationScreens` 入口 |

## 向 Saturn 注册展示行

展示扩展统一注册到 Saturn，所有前端共享。每次注册一条逻辑行，必须指定 `InfomationGroup`：

```java
import com.iafenvoy.saturn.view.PlayerInformation;
import com.iafenvoy.saturn.view.model.InfomationGroup;

InfomationGroup cultivation = PlayerInformation.registerGroup(
        Identifier.parse("mxt:cultivation"),
        Component.translatable("info.mxt.cultivation"), 100);

PlayerInformation.register(Identifier.parse("mxt:realm"), cultivation, 0,
        (view, context) -> Optional.of(new InformationRowContent(
                Component.translatable("info.mxt.realm"),
                new InformationValue.Text(Component.literal("example")),
                Set.of())));
```

这是扩展方示例，MXT 尚未在本次改动中迁移；示例翻译键由扩展方提供。实际 provider 应读取该模组注册的类型化字段，并在 `sourceFields` 中声明来源，不能在回调里读当前玩家或重新执行玩法结算。

- 注册在模组初始化阶段完成。注册器支持同步登记，不在回调执行期间持有全局锁。
- 行 ID、分组、order 由注册器持有，provider 只返回内容。ID 重复会拒绝，不允许后注册者覆盖。
- 分组按 order、ID 排序，组内行按 order、ID 排序。`InfomationGroup` 的身份只取 ID，不受标题语言影响。
- `Optional.empty()` 表示隐藏；需要展示“未记录”时返回 `InformationValue.Missing`。
- 列表、表格和物品集合属于一条逻辑行的内容，可以由界面展开为多行。动态数据不修改全局注册表。
- 单行 provider 抛出运行时异常会记录并跳过该行，同组其他行继续生成。

内置分组常量均为 `InfomationGroup`：`BASIC`、`STATUS`、`EQUIPMENT`。第三方可以加入这些组或注册自己的组。

## 字段与缺失值

`PlayerView.read(field)` 返回 `ViewValue<T>`：

- `value()` 有值表示已知，零和空集合均是有效值。
- `missingReason()` 说明未记录、未请求、不支持、隐藏、无效或不适用。
- 已知值的 `origin()` 区分捕获值、保存值和根据当前规则重建的值。

快照在 `put` 时通过字段 Codec 编码，`read` 时独立解码，因此不会持有活玩家、可变附件或调用方集合。返回值即使可以修改，也不影响快照。`PlayerViewItem` 的 NBT 与 Component、展示模型的 Component 同样复制后返回。

标准字段覆盖身份、生命/吸收/氧气、食物、经验、位置、游戏模式、选中快捷栏、装备、背包、末影箱、鼠标物品、效果、属性、Saturn 资源和能力、个人分数及队伍。

`INVENTORY` 仅为普通背包槽，装备另在 `EQUIPMENT`；空槽从集合省略。保存数据中的装备以 MC 26 `equipment` 为准，主手从 `Inventory` 与 `SelectedItemSlot` 补出。效果持续时间和资源边界均为输入时的数据，不随查看时间继续结算。

Saturn 不为读取界面创建附件，也不初始化资源。保存的属性仅能确认基值时，有效值保持缺失；不会根据当前规则猜出历史最大健康。保存的物品保留原始数据和 ID，即使原物品模组已删除也能显示 ID；真正构造渲染用物品时才调用 `PlayerViewItem.stack(registries)`，失败返回空。

## 注册类型化扩展

```java
PlayerViewField<MyData> field = PlayerViewFields.register(
        Identifier.parse("mymod:details"), 1, MyData.CODEC);
```

然后实现并注册 `PlayerViewContributor`，声明自己拥有的字段，在 `capture(Player, PlayerViewWriter)` 中只读解析传入实体：

```java
PlayerViews.registerContributor(myContributor);
PlayerViews.registerReader(mySavedReader);
```

`SavedPlayerViewReader` 是可选的，适用于读取传入旧 NBT 的扩展。注册时冻结字段归属，同一种解析路径不能有两个提供者拥有同一个字段。受限 writer 拒绝写入其他模块字段和重复写同一字段。

调用实体解析时遵守实体线程要求：服务端 Player 必须在服务端主线程，客户端 Player 由调用方保证在客户端线程。采集器不能触发加点、资源初始化、随机推进或其他结算。

## 解析调用方提供的保存数据

```java
var context = new SavedPlayerViewContext(meta, registries, playerNbt,
        Optional.of(statsJson), Optional.of(advancementsJson));
var view = PlayerViews.parse(context);
var supplemented = PlayerViews.parse(context, Optional.of(recordedView));
```

`meta` 由调用方提供，包括账号、来源、记录 ID、快照 ID、真实捕获时间（可空）及本次组装时间。前世应使用不同的记录 ID，不能仅以 UUID 区分缓存。

传入的 recordedView 优先，旧档读取器只补完全没有保存的字段，且必须与 context 引用同一记录。字段缺少与字段损坏分别表示 `NOT_CAPTURED` 和 `INVALID`；单个内置字段损坏不会阻断其他内置字段。

原版玩家存档不包含完整 GameProfile；需要姓名、皮肤属性、单独保存的鼠标物品、计分板等内容时，调用方可先向 builder 放入已知字段，再交给解析器补缺项。统计与进度可通过 context 的独立 JSON 文本传入。实体解析不会访问原版私有统计/进度存储，也不会调用保存或创建进度的方法，这两项在该路径标为 `UNSUPPORTED`。

输入 NBT 应已由调用方完成必要的原版/模组版本迁移；内置读取器按 MC 26 当前结构解析，不自行定位文件或调用 DataFixer。传入摘要就只显示摘要中能确认的事实。

## Codec 与宿主集成

`PlayerViewSnapshot.CODEC` 提供 NBT/JSON 编解码，调用方需要注册表上下文时使用 `registries.createSerializationContext(ops)`。Codec 不执行磁盘读写。

字段封套独立携带 schema、known/missing 状态、来源及载荷。未知字段封套保留，未安装其模组也能往返；schema 不支持或字段无效只影响该字段。固定顶层未知键按普通 Codec 规则忽略，不承诺保留。

`snapshot.filter(allowedField)` 在实际发送前移除不允许的字段，包括未知扩展；信息文档的 `filter` 按行来源字段过滤。宿主应基于实际连接授权，不能信任客户端自报身份。

`PlayerViewRequest`、`PlayerViewResult` 与 `PlayerViewAccess` 是可选的宿主查询契约。Saturn 没有全局网络安装器，也不自行查询账号/归档。请求 ID 供宿主丢弃过期响应；调用方明确列出需要的字段，空列表不隐式授予全部数据。

## 默认界面与接入边界

仅客户端调用：

```java
minecraft.setScreen(PlayerInformationScreens.create(parent, view));
minecraft.setScreen(PlayerInformationScreens.create(parent, view, group));
```

默认页面显示传入快照的通用文字行、列表、物品槽位文本和表格，支持滚动和 tooltip。`InformationText` 提供同一套文字投影，AUI/卡片也可以直接渲染类型化 `InformationValue`。

该页面不自动刷新、不请求数据、不提供写操作。人物三维外观预览、加载控制和网络刷新由后续客户端适配完成；当前页面不会按历史 UUID 借用今世实体。MXT、System、Reincarnation 的业务迁移也不包含在本次 Saturn 基础模块中。

## 验证

```powershell
.\gradlew.bat compileJava verifyPlayerViews build --console=plain
```

`verifyPlayerViews` 位于独立的 `src/verification/java` 源码集，不进入发布 JAR，不启动游戏、不构造 Player、不创建世界，也不读写玩家存档。覆盖按分组/全取、顺序与重复注册、故障隔离、快照复制、NBT/JSON 往返、未知扩展、字段 schema、保存数据缺失与损坏、字段归属等。

该检查使用无需游戏 bootstrap 的数据和数字展示模型；完整 Component 编解码、在线实体采集和界面渲染仍需游戏环境验收，不能据此宣称已经实机通过。
