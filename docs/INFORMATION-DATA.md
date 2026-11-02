# 数据驱动的玩家信息行

信息行通过数据包定义，从调用方传入的 `PlayerView` 生成扁平的 `InformationEntry`。`InformationRow` 是行定义，`InformationGroup` 是引用行的组定义；结果通过 Holder 直接引用这两种定义，不再复制另一套 Row/Group 元数据。参数化 getter 负责确认实体事实；表达式负责取值、计算和组织展示，不寻找实体、不读取玩家文件，也不运行玩法结算。

## 注册表与目录

| 注册表 | Java 入口 | 内容 |
| --- | --- | --- |
| `saturn:entity_value_getter` | `SaturnRegistries.ENTITY_VALUE_GETTER` | 代码注册表，值为 `MapCodec<? extends EntityValueGetter<?>>`，用于解码查询参数 |
| `saturn:information_expression_type` | `SaturnRegistries.INFORMATION_EXPRESSION_TYPE` | 代码注册表，值为 `MapCodec<? extends InformationExpression>` |
| `saturn:information_group` | `SaturnResourceKeys.INFORMATION_GROUP` | 原生数据包注册表，值为 `InformationGroup` |
| `saturn:information_row` | `SaturnResourceKeys.INFORMATION_ROW` | 原生数据包注册表，值为 `InformationRow` |

定义文件路径：

```text
data/<namespace>/saturn/information_group/<path>.json
data/<namespace>/saturn/information_row/<path>.json
data/<namespace>/tags/saturn/information_row/<path>.json
```

文件的 namespace 和 path 决定条目 ID，文件中不再写 `id`。Group 和行定义通过原生注册表同步；玩家事实、信息文档及其权限和网络仍由宿主处理。这两种定义遵循原生动态注册表的世界加载生命周期，修改后需要重新加载世界/重启服务器；没有额外实现 `/reload` 热更新服务。

内置 3 个 Group 和 16 条信息行均位于 `src/main/resources/data/saturn/saturn/`，可以直接参考和覆盖。所有信息行和组都由这两个数据包注册表加载，代码只注册 getter / expression 等固有类型。

## Group 引用行与标签

**成员关系由 Group 声明，Row 定义不含 groups。** Group 的 `rows` 是数组，每个元素在 Java 中为：

```java
Either<Holder<InformationRow>, TagKey<InformationRow>>
```

Codec 复用 `RegistryCodecs.holderOrTag(SaturnResourceKeys.INFORMATION_ROW).listOf()`。例如 `data/example/saturn/information_group/overview.json`：

```json
{
  "title": { "translate": "info.example.group.overview" },
  "order": 10,
  "rows": ["saturn:health", "#example:overview"]
}
```

`title` 为 Component，也支持翻译键字符串（如 `"info.example.group.overview"`）；`order` 默认为 `0`，`rows` 为必填数组，可为空。普通 ID 解码为注册表 Holder，`#` 前缀解码为 TagKey；不支持在 Group 中内联 Row 定义。直接引用不存在的 Row 会在原生加载/注册表校验时失败；不存在的标签按空集合处理。加载期保留标签引用，收集时根据当前行注册表展开，不把标签内容永久复制到 Group。

对应标签 `data/example/tags/saturn/information_row/overview.json`：

```json
{
  "replace": false,
  "values": ["saturn:health", "saturn:experience"]
}
```

内置 basic/status/equipment 三组分别引用 `#saturn:basic`、`#saturn:status`、`#saturn:equipment`。内容模组可以通过同名标签追加自己的数据行，无需覆盖整个 Group；也可以定义新标签、新 Group。标签遵循原生追加、replace、嵌套标签和可选条目的规则。

- Row 只定义一次，多个 Group 可引用同一 Row 或重叠标签，收集器反向计算生成结果的组列表。
- 单组查询展开其 `rows`；多组选取为并集。直接引用、标签及多组重叠均按 Row ID 去重，同次收集每条行只求值一次。空的组选取集合仍表示不取任何行。
- `collectAll` 收集全部启用且满足显示条件的 Row，包括没有被任何 Group 引用的行；未分组行结果中的 `groups` 为空。
- Group 按 `order`、ID 排序。全取和多组并集先按生成结果所属组中排序最靠前的组排列，再按行 `order`、ID 排列；未分组行放在最后，按行 `order`、ID 排序。
- 单组查询和 `document.rows(group)` 均按行 `order`、ID 排序。Group 的 rows/标签展开顺序只决定选取范围，显示顺序仍由 Row order 决定。
- 默认分组页面在每个引用该 Row 的组中展示它，未分组行显示在“其他信息”下；按组打开只展示所选组。平面卡片遍历 `document.rows()` 可以避免重复展示。

`PlayerInformation.groups(view[, context])` 从当前注册表取得 `Holder<InformationGroup>` 用于筛选，标题与顺序直接读取其 value；组选择应使用相同注册表上下文的 Holder。数据包按原生优先级覆盖同 ID Group 的整份定义；从组中移出的行仍可全取。

## 行定义

生命行示例：

```json
{
  "order": 10,
  "importance": 100,
  "label": { "translate": "info.saturn.field.health" },
  "value": {
    "type": "saturn:coalesce",
    "values": [
      {
        "type": "saturn:meter",
        "value": { "type": "saturn:getter", "getter": { "type": "saturn:health" } },
        "min": 0,
        "max": { "type": "saturn:getter", "getter": { "type": "saturn:max_health" } }
      },
      {
        "type": "saturn:number",
        "value": { "type": "saturn:getter", "getter": { "type": "saturn:health" } }
      }
    ]
  }
}
```

| 字段 | 默认值 | 含义 |
| --- | --- | --- |
| `label` | 必填 | 静态 Component 标题 |
| `value` | 必填 | 生成内容的表达式 |
| `order` | `0` | 行顺序 |
| `importance` | `0` | 任意 int，越大越重要，与顺序独立 |
| `enabled` | `true` | 是否启用；`false` 不生成该行 |
| `label_expression` | 无 | 动态标题，成功时覆盖 `label` |
| `tooltip` | 无 | 静态 Component 提示 |
| `tooltip_expression` | 无 | 动态提示，成功时覆盖 `tooltip` |
| `tone` | `normal` | `normal`、`positive`、`warning` |
| `visible` | 无 | 布尔表达式；false、缺失或类型不符时隐藏行 |
| `missing` | `show` | `show` 展示缺失原因，`hide` 在最终内容为 Missing 时隐藏 |

动态标题/提示不可用时保留静态回退；其字段依赖仍会保留。`missing: hide` 只判断整条内容，列表或表格中的缺失单元不会让整行消失。禁用定义仍需满足完整结构，例如包含 `label` 和 `value`。Row 不声明所属 Group；原型格式的未知 `groups` 键按普通 Codec 规则忽略，应把成员关系迁到 Group 的 rows 或行标签。

数据包同 ID 覆盖按原生包优先级选择整个定义，不做 JSON 属性合并。所有 Row 都来自当前注册表，无 Java provider 回退。Group 仅选择行，不改变行的 importance 或表达式。

## 表达式如何工作

`InformationExpression` 是带 `MapCodec` 的操作节点。取值节点返回中间数据；展示节点生成现有 `InformationValue`。同一个 getter 结果可用于数字、进度条、翻译参数及表格，无需为每个取值逻辑注册专属展示 Type。

数字、字符串和布尔值可直接作为常量：`0`、`"example"`、`false`。结构化常量使用 `constant` 包装。所有操作对象均显式写 `type`；未知 Type 解码失败，不回退到注册表默认项。

```json
{
  "type": "saturn:getter",
  "getter": { "type": "saturn:experience" },
  "path": ["level"]
}
```

`getter` 对象通过 `EntityValueGetter.CODEC` 和原生 `saturn:entity_value_getter` 解码参数，再执行 `view.read(getter)` → `getter.valueCodec()` 编码 → 按可选 path 取值 → 消费节点按预期类型解码。这里只读取传入视图，不调用 live capture。schema、缺失状态及载荷检查继续生效。未知类型在定义解码时失败，参数缺失或无效也返回 Codec 错误；快照中的未知原始查询仍可保留往返。扩展见 [Getter 注册](PLAYER-VIEWS.md#注册类型化扩展)。

例如读取特定属性的有效值，可以直接写：

```json
{
  "type": "saturn:number",
  "value": {
    "type": "saturn:getter",
    "getter": {
      "type": "saturn:attribute",
      "attribute": "minecraft:max_health",
      "value": "effective"
    }
  }
}
```

属性按 ID 匹配，不依赖列表顺序。还内置 `resource`（resource ID、current/minimum/maximum）、`effect`（effect ID、amplifier/duration）和 `score`（objective 名称）。参数完整定义参与快照及求值缓存身份，不同属性、不同模式不会串数据。无确切结果时，内置查询可从快照的 attributes/resources/effects/scores 投影；明确 missing、无效载荷或未知 schema 不被投影覆盖。历史未记录的有效属性值、资源边界保持缺失。

`PlayerViews.parse` 会采集当前世界启用行需要的 getter（包括条件全部分支、标题和 tooltip），并合并默认/显式查询与依赖。手工构造或既有归档不会因新行定义而读取活实体，缺少数据仍返回相应 Missing。

`path` 默认为空，仅用于复合结果的成员访问。对象按键名，数组用非负十进制索引字符串（如 `["0", "id"]`）；包含点号或冒号的键保持一个元素。按属性/资源 ID 取单个成员应使用带参数 getter。缺少成员/索引越界为 `NOT_CAPTURED`，结构或类型不符为 `INVALID`；查询本身缺失时保留原始原因。

零、false、空集合都是已知值。数值转换拒绝非数值、NaN 和 Infinity。结果 Codec 不提供通用的可检查结构描述，路径存在性和结果类型在求值时判断。同一次收集按完整 getter 参数缓存编码结果，各行共享，不保存为全局玩家缓存。

### 内置 Type

实现位于 `com.iafenvoy.saturn.data.information.expression.builtin`，每个 Type 单独一个类，例如 `GetterExpression`、`MeterExpression`、`TableExpression`。它们各自持有 MapCodec，由 `registry.content.SaturnInformationExpressions` 统一注册；表格列 DTO 随 `TableExpression` 定义，公共辅助方法收在包内的 `ExpressionSupport`。

下表所有 ID 均带 `saturn:` 前缀；标为“表达式”的参数允许嵌套任意操作或原始常量。

| Type | JSON 参数 | 结果 |
| --- | --- | --- |
| `constant` | `value`：任意数据 | 中间数据；数组/对象常量必须使用此包装 |
| `getter` | `getter`：带 type/参数的查询对象，`path`：字符串数组，默认 `[]` | 查询结果 Codec 数据 |
| `local` | `name`：默认 `item`，`path`：默认 `[]` | 列表/表格的当前元素或键 |
| `number` | `value`：表达式 | Number |
| `boolean` | `value`：表达式 | Flag |
| `text` | `value`：表达式，`component`：默认 false | Text；默认接受标量文字或 Component，true 强制使用 Component Codec |
| `items` | `value`：表达式 | Items；按 `Map<String, PlayerViewItem>` Codec 解码 |
| `meter` | `value`、`max`：表达式，`min`：表达式，默认 `0` | Meter；要求 max ≥ min |
| `translate` | `key`：字符串表达式，`arguments`：表达式数组默认 `[]`，`fallback`：可选字符串表达式 | 延迟翻译的 Component；保留 Component 参数和样式 |
| `format` | `format`：格式字符串，`arguments`：表达式数组 | 中间字符串；`String.format(Locale.ROOT, ...)`，数字参数为 double，使用 `%f`/`%.1f` 等 |
| `arithmetic` | `operation`：`add/subtract/multiply/divide/min/max`，`left`、`right`：表达式 | 中间数值；除零等非有限结果为 INVALID |
| `compare` | `operation`：`eq/ne/lt/le/gt/ge`，`left`、`right`：表达式 | 布尔值；eq/ne 比较标量或文字，其余比较数字 |
| `logic` | `operation`：`and/or/not`，`values`：表达式数组 | 布尔值；and/or 短路，not 必须恰好一个操作数 |
| `is_known` | `value`：表达式 | 是否已知；已知的 false/0/空集合返回 true |
| `if` | `condition`、`then`、`else`：表达式 | 仅求值所选分支；条件缺失时传播缺失 |
| `coalesce` | `values`：表达式数组 | 第一个已知结果；全部缺失时保留首项缺失原因，空数组为 NOT_CAPTURED |
| `list` | `source`、`element`：表达式，`as`：局部名默认 `item` | Values |
| `table` | `source`：表达式，`columns`：列数组，`as`：默认 `item`，`row_id`：可选字符串表达式 | Table |

最终 value 若直接返回中间标量，会自动投影为 Text、Number 或 Flag。对象和数组需要明确通过 `text`、`items`、`list` 或 `table` 消费，避免把整个结构盲目转成字符串。

`translate` 保留翻译键，在客户端根据语言显示；`format` 生成固定字符串，Component 参数会转成当前环境的文字，不适合代替延迟翻译。翻译数字参数的整数值会以整数形式传入，避免效果等级出现 `1.0`。

### 列表与表格作用域

`source` 可以是数组或对象。数组保留顺序；对象按键排序。`local` 默认读取 `item`；`item_key` 表示当前索引/对象键，值为字符串。指定 `as: "effect"` 后对应变量为 `effect` 和 `effect_key`。嵌套作用域可以用不同名字保留外层元素。

资源表格示例：

```json
{
  "type": "saturn:table",
  "source": { "type": "saturn:getter", "getter": { "type": "saturn:resources" } },
  "row_id": { "type": "saturn:local", "path": ["id"] },
  "columns": [
    {
      "id": "name",
      "label": { "translate": "info.saturn.field.name" },
      "value": { "type": "saturn:text", "value": { "type": "saturn:local", "path": ["id"] } }
    },
    {
      "id": "value",
      "label": { "translate": "info.saturn.field.value" },
      "value": { "type": "saturn:number", "value": { "type": "saturn:local", "path": ["value"] } }
    }
  ]
}
```

列 `id` 必须唯一，列 `label` 为静态 Component，列 `value` 为表达式。省略 `row_id` 时使用数组索引/对象键；重复行 ID 为 INVALID。单元格可以是数字、文字、进度条等任意 InformationValue；单元格缺失保持为 Missing。内置资源定义进一步用 coalesce 在有边界时显示进度条，边界缺失时显示数字。

`InformationEvaluation` 每行限制 64 层求值深度、100,000 次节点求值；一次集合展开最多 10,000 个元素。超限为 INVALID。Type 的运行时异常由行收集器记录并隔离，其他行继续生成。

## 来源字段与授权

节点通过 `getters()` 合并全部子表达式的查询；默认 `sourceFields()` 从 getter 的类型 ID 与 dependencies 推导来源。行合并 value、visible、动态标题和 tooltip 的全部依赖，包括条件未选中的分支。局部变量依赖由外层列表/表格的 source 及子表达式覆盖。历史命名 `sourceFields` 保留，其粒度是 getter 类型 ID；例如参数化 attribute 同时声明 `saturn:attribute` 与 `saturn:attributes`，不提供单属性参数授权。

`document.filter(allowedGetterType)` 删除依赖未授权类型的整条行；snapshot.filter 同时过滤 fields / queries。宿主应在传输前过滤实际数据与文档，需要更细粒度时自行限制投影。JSON 显示条件不是权限控制。自定义 Type 必须通过 getters 声明完整依赖，避免遗漏采集或来源。

## 注册自定义 Type

实现 `InformationExpression`，注册自己的 `MapCodec`。示例只对传入的数值做限制，不绑定特定玩家字段：

```java
public record Clamp(InformationExpression value, double min, double max) implements InformationExpression {
    public static final MapCodec<Clamp> CODEC = RecordCodecBuilder.<Clamp>mapCodec(i -> i.group(
            InformationExpression.CODEC.fieldOf("value").forGetter(Clamp::value),
            MiscCodecs.FINITE.fieldOf("min").forGetter(Clamp::min),
            MiscCodecs.FINITE.fieldOf("max").forGetter(Clamp::max)
    ).apply(i, Clamp::new)).validate(v -> v.min <= v.max
            ? DataResult.success(v) : DataResult.error(() -> "max must be >= min"));

    @Override public MapCodec<Clamp> codec() { return CODEC; }
    @Override public Set<EntityValueGetter<?>> getters() { return value.getters(); }
    @Override public ExpressionValue evaluate(InformationEvaluation context) {
        double number = context.evaluate(value).number();
        return context.data(context.ops().createDouble(Math.clamp(number, min, max)));
    }
}

public static final DeferredRegister<MapCodec<? extends InformationExpression>> EXPRESSIONS =
        DeferredRegister.create(SaturnRegistries.INFORMATION_EXPRESSION_TYPE, "example");

static {
    EXPRESSIONS.register("clamp", () -> Clamp.CODEC);
}

// 在本模组构造器中：
// EXPRESSIONS.register(modEventBus);
```

常用导入为 `com.iafenvoy.saturn.data.information.expression.*`、`data.information.getter.EntityValueGetter`、`SaturnRegistries`、`MiscCodecs`、DFU 的 Codec/MapCodec/DataResult/RecordCodecBuilder 及 NeoForge 的 DeferredRegister。节点对子节点调用 `context.evaluate(...)`，使用其类型转换方法；缺失值会传播为中间 Missing。若自行实现循环，应遵守同样的只读、有界求值约定。

总 `InformationExpression.CODEC` 使用 `Codec.lazyInitialized` 延迟创建分发器，解决节点 MapCodec 与总入口的静态初始化相互引用。它没有使用一个未被消费的 `Codec.recursive` self 参数。

## 调用上下文与兼容迁移

```java
// 从实体或带 RegistryOps 解码的快照取得原有上下文：
var view = PlayerViews.parse(player);
var all = PlayerInformation.collectAll(view);
var basicGroup = PlayerInformation.groups(view).stream()
        .filter(group -> group.unwrapKey().orElseThrow().identifier().equals(Identifier.parse("saturn:basic"))).findFirst().orElseThrow();
var basic = PlayerInformation.collect(view, basicGroup);
var groups = PlayerInformation.groups(view);

// 手工创建、没有注册表上下文的视图，由宿主显式提供当前世界注册表：
var context = new InformationContext("zh_cn", registries);
var allWithDefinitions = PlayerInformation.collectAll(view, context);
var selected = PlayerInformation.collect(view, context, Set.copyOf(PlayerInformation.groups(view, context)));
```

`InformationContext` 显式传入的 provider 优先；否则读取 `PlayerView.serializationOps()` 中随视图携带的 RegistryOps。默认接口实现返回 JsonOps，`PlayerViewSnapshot` 会保留自身编解码上下文。没有注册表的视图返回空行和空组列表，不会从某个全局世界补入定义，也不会偷偷读取当前客户端玩家。跨世界使用或希望使用当前定义时，应明确传当前 provider。

`groups(view[, context])` 枚举当前 Group 注册表，包括空组；`document.groups()` 只包含实际生成行所属的组。成员关系始终来自当前注册表定义，不存在独立组元数据对象。手工视图也可以显式传入只含 Row 注册表的 provider，此时全取结果为未分组行。

旧 `PlayerInformation.register*`、`PlayerInformationRegistry`、`PlayerInformationRowProvider` 和无参代码组枚举已删除。将旧 provider 逻辑迁到 getter / expression 的固有类型注册表，并通过 Row/Group JSON 引用，不再存在代码行/组与数据定义合并的路径。内置 `BASIC` / `STATUS` / `EQUIPMENT` 元数据常量也已移除，按 ID 从当前 Group 注册表查询。

求值结果统一为 `InformationEntry(row, groups, label, value, tooltip)`：row 为 `Holder<InformationRow>`，groups 为 `Set<Holder<InformationGroup>>`。直接读取 `entry.value()`、`entry.label()` 和 `entry.tooltip()`；`entry.id()/order()/importance()/tone()/sourceFields()` 从 Row Holder 取得定义信息。旧重复组模型、Result/Content 嵌套及相应的转换方法已删除。

定义与引用的 Codec 采用其他模块的相同约定：`InformationRow.DIRECT_CODEC` / `InformationGroup.DIRECT_CODEC` 用于数据包对象，`CODEC` 用于注册表 Holder。生成结果的 Codec 编码 row ID、groups ID 数组和 label/value/tooltip，不复制定义的标题、排序、importance 或依赖来源。编解码文档须使用包含对应定义的 RegistryOps；长期归档保存 `PlayerViewSnapshot`，展示时根据当前定义重新收集。Row/Group 数据包格式保持不变；旧展示结果格式已移除，网络两端应使用匹配版本。

旧 `saturn:field` 表达式需迁为 `saturn:getter`，例如把 `"field":"saturn:health"` 换成 `"getter":{"type":"saturn:health"}`；path 可保持。旧 player_view_field 代码注册表及 Field API 已移除。固定选项使用 enum Codec（getter 的 value、算术/比较/逻辑 operation、missing、展示值 type 等），非法枚举值解码为 DataResult 错误。

快照保留 version 1 和基础 fields，新带参数结果存可选 queries。旧基础封套可读，未知 getter 定义/载荷无损保留；旧版会忽略 queries，无法使用或保留参数结果。详见 [快照与宿主集成](PLAYER-VIEWS.md#codec-与宿主集成)。

## 指令检查输出

```mcfunction
/information query @s
/information query @s saturn:basic
/information gui @s
/information gui @s saturn:basic
```

两个分支都要求管理员权限（`Permissions.COMMANDS_ADMIN`，3 级），target 支持玩家名、UUID 和单实体选择器，group 支持补全。省略 group 全取，指定 group 按组筛选，两种模式返回逻辑行数。`query` 使用 PlayerViews → PlayerInformation → InformationText 路径，只向执行者输出标题及各行文字；`gui` 要求玩家执行，将同一份已求值文档及分组选择只发送给执行者，打开默认信息界面，不自动刷新。未知组报错，空组显示暂无信息。支持非玩家实体，玩家专用数据按缺失策略处理。对应的 `/saturn information query/gui ...` 始终保留；顶层别名由默认开启的 `commands.information` 配置控制，重名时跳过。

## 验证

```powershell
.\gradlew.bat compileJava verifyPlayerViews runGameTestServer build --console=plain
```

`verifyPlayerViews` 验证独立核心契约；main 源码集的 `gametest.InformationTests` / `EntityValueGetterTests` / `InformationCommandTests` 验证 getter/表达式/行/Group 注册表、16 条 JSON、带参及默认查询、实时与存档解析、NBT/JSON 往返、Component/物品、缺失投影、路径、条件、覆盖、Holder/Tag、展开去重、依赖过滤和扁平结果的 Holder 引用 Codec。指令通过真实 dispatcher 执行并核对文字输出、分组、补全、错误、权限与非玩家查询。GameTest 仅在 NeoForge 启用测试时注册，不使用 test-mod；客户端视觉效果仍需人工验收。
