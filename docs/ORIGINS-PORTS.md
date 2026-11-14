# Origins 通用能力移植（Saturn 1.3.1）

从 Origins-NeoForge 1.21.1 中复用通用设计与实现，适配 Saturn 的 Minecraft 26.1.2 上下文和类型注册表，不引入 Origins 运行时依赖。新增 106 个注册项，同名类型在不同上下文分别计数。

| 类型族 | 新增数量 |
| --- | --- |
| EntityAction | 17 |
| BiEntityAction | 8 |
| BlockAction | 8 |
| ItemAction | 5 |
| EntityCondition | 34 |
| BiEntityCondition | 8 |
| BlockCondition | 16 |
| ItemCondition | 7 |
| DamageCondition | 3 |

## 新增类型

| 类型族 | ID | 行为 |
| --- | --- | --- |
| EntityCondition | `saturn:attacker_condition` | 实体最近受到过生物攻击 |
| EntityCondition | `saturn:attack_target_condition` | 生物当前有攻击目标 |
| EntityCondition | `saturn:climbing` | 实体正在攀爬 |
| EntityCondition | `saturn:collided_horizontally` | 实体与方块发生水平碰撞 |
| EntityCondition | `saturn:creative_flying` | 玩家正在使用创造飞行 |
| EntityCondition | `saturn:daytime` | 当前为白天 |
| EntityCondition | `saturn:exists` | 实体尚未被移除 |
| EntityCondition | `saturn:fall_flying` | 实体正在滑翔 |
| EntityCondition | `saturn:grounded` | 实体接触地面 |
| EntityCondition | `saturn:hostile` | 实体属于敌对生物 |
| EntityCondition | `saturn:in_rain` | 实体当前淋雨 |
| EntityCondition | `saturn:invisible` | 实体处于隐身状态 |
| EntityCondition | `saturn:living` | 实体是生物 |
| EntityCondition | `saturn:on_fire` | 实体正在燃烧 |
| EntityCondition | `saturn:raining` | 当前维度正在下雨 |
| EntityCondition | `saturn:sprinting` | 实体正在疾跑 |
| EntityCondition | `saturn:swimming` | 实体正在游泳 |
| EntityCondition | `saturn:tamed` | 实体已经拥有主人 |
| EntityCondition | `saturn:thundering` | 当前维度处于雷暴 |
| BlockCondition | `saturn:air` | 目标是空气 |
| BlockCondition | `saturn:attachable` | 至少一个相邻方块朝向目标的面可以支撑附着物 |
| BlockCondition | `saturn:block_entity` | 目标位置具有方块实体 |
| BlockCondition | `saturn:exposed_to_sky` | 目标位置可见天空 |
| BlockCondition | `saturn:in_rain` | 目标位置正在淋雨 |
| BlockCondition | `saturn:light_blocking` | 目标方块可遮挡光线 |
| BlockCondition | `saturn:raining` | 当前维度正在下雨 |
| BlockCondition | `saturn:replaceable` | 目标方块可以被替换 |
| BlockCondition | `saturn:thundering` | 当前维度处于雷暴 |
| BlockCondition | `saturn:water_loggable` | 方块支持含水状态 |
| ItemCondition | `saturn:block_item` | 物品是方块物品 |
| ItemCondition | `saturn:empty` | 物品堆为空 |
| ItemCondition | `saturn:enchantable` | 物品可通过附魔台附魔 |
| ItemCondition | `saturn:food` | 物品具有食物组件 |
| ItemCondition | `saturn:is_damageable` | 物品具有耐久度 |
| BiEntityCondition | `saturn:attacker` | actor 是最近攻击 target 的生物 |
| BiEntityCondition | `saturn:attack_target` | target 是 actor 的攻击目标 |
| BiEntityCondition | `saturn:equal` | 两个参数指向同一实体 |
| BiEntityCondition | `saturn:owner` | actor 是 target 的主人 |
| BiEntityCondition | `saturn:riding` | actor 正在直接骑乘 target |
| BiEntityCondition | `saturn:riding_root` | target 是 actor 最底层的载具 |
| EntityCondition | `saturn:constant` | 返回配置的固定布尔值 |
| BiEntityCondition | `saturn:constant` | 返回配置的固定布尔值 |
| BlockCondition | `saturn:constant` | 返回配置的固定布尔值 |
| ItemCondition | `saturn:constant` | 返回配置的固定布尔值 |
| DamageCondition | `saturn:constant` | 返回配置的固定布尔值 |
| EntityAction | `saturn:for_range` | 将动作重复执行指定次数，负数为零次，次数不超过 max_iterations |
| EntityAction | `saturn:if_else_list` | 按顺序检查条件，只执行第一个命中分支 |
| EntityAction | `saturn:side` | 仅在指定逻辑端执行，不以客户端或服务端进程判断 |
| EntityAction | `saturn:while` | 条件成立时重复执行，超过 max_iterations 则终止并报告异常 |
| BiEntityAction | `saturn:for_range` | 将动作重复执行指定次数，负数为零次，次数不超过 max_iterations |
| BiEntityAction | `saturn:if_else_list` | 按顺序检查条件，只执行第一个命中分支 |
| BiEntityAction | `saturn:side` | 仅在指定逻辑端执行，不以客户端或服务端进程判断 |
| BiEntityAction | `saturn:while` | 条件成立时重复执行，超过 max_iterations 则终止并报告异常 |
| BlockAction | `saturn:for_range` | 将动作重复执行指定次数，负数为零次，次数不超过 max_iterations |
| BlockAction | `saturn:if_else_list` | 按顺序检查条件，只执行第一个命中分支 |
| BlockAction | `saturn:side` | 仅在指定逻辑端执行，不以客户端或服务端进程判断 |
| BlockAction | `saturn:while` | 条件成立时重复执行，超过 max_iterations 则终止并报告异常 |
| ItemAction | `saturn:for_range` | 将动作重复执行指定次数，负数为零次，次数不超过 max_iterations |
| ItemAction | `saturn:if_else_list` | 按顺序检查条件，只执行第一个命中分支 |
| ItemAction | `saturn:side` | 仅在指定逻辑端执行，不以客户端或服务端进程判断 |
| ItemAction | `saturn:while` | 条件成立时重复执行，超过 max_iterations 则终止并报告异常 |
| BiEntityAction | `saturn:invert` | 交换 actor 与 target 后执行动作 |
| BiEntityCondition | `saturn:invert` | 交换 actor 与 target 后检查条件 |
| ItemAction | `saturn:holder_action` | 对物品持有者执行实体动作 |
| BlockAction | `saturn:relative_offset` | 沿上下文给定的方块面偏移后执行；没有方向时不执行 |
| EntityCondition | `saturn:fluid_height` | 比较实体浸入指定流体类型的高度 |
| EntityCondition | `saturn:submerged_in` | 检查实体眼睛是否浸没在指定流体类型中 |
| BlockCondition | `saturn:fluid` | 检查目标位置的流体 ID |
| DamageCondition | `saturn:attacker` | 检查伤害来源中的攻击者 |
| DamageCondition | `saturn:amount` | 比较本次伤害数值 |
| EntityAction | `saturn:damage` | 使用指定伤害类型对实体造成伤害，金额可用数值提供器 |
| BiEntityAction | `saturn:damage_target` | 使用指定伤害类型对实体造成伤害，金额可用数值提供器 |
| EntityAction | `saturn:execute_command` | 以实体身份在激活位置执行原版命令，权限等级为 2 |
| BlockAction | `saturn:execute_command` | 在目标方块中心执行原版命令，权限等级为 2，没有 @s 实体 |
| EntityCondition | `saturn:command` | 执行命令并比较其返回值，仅服务端有效；命令自身可能产生副作用 |
| EntityCondition | `saturn:advancement` | 检查玩家是否完成指定原版进度，仅服务端有效 |
| EntityAction | `saturn:grant_advancement` | 授予原版进度或指定判据 |
| EntityAction | `saturn:revoke_advancement` | 撤销原版进度或指定判据 |
| EntityCondition | `saturn:stat` | 比较玩家原版统计值，支持自定义统计 ID 或带 stat_type/id 的类型统计 |
| EntityAction | `saturn:award_stat` | 增加玩家的一项或多项原版统计值 |
| EntityAction | `saturn:reset_stat` | 将玩家的一项或多项原版统计值重置为零 |
| EntityCondition | `saturn:nbt` | 按 NBT 子集匹配实体保存数据，列表使用子集匹配 |
| BlockCondition | `saturn:nbt` | 按 NBT 子集匹配目标方块实体的数据，目标没有方块实体则为 false |
| EntityCondition | `saturn:predicate` | 引用原版数据包 predicate，缺失或在客户端检查时为 false |
| EntityCondition | `saturn:inventory` | 统计实体原版背包与装备中满足条件的物品数量或堆数，玩家主手不会重复计数 |
| EntityAction | `saturn:modify_inventory` | 对筛选出的非空原版槽位各执行一次物品动作与可选实体动作；limit 为零或负数时处理全部匹配槽位 |
| EntityAction | `saturn:replace_inventory` | 将筛选出的槽位替换为独立创建的物品模板，允许显式匹配空槽 |
| EntityCondition | `saturn:entity_in_radius` | 统计指定范围内满足条件的非旁观实体，包含自身；形状按相对查询中心的距离计算 |
| EntityCondition | `saturn:block_in_radius` | 统计实体周围符合条件的方块，位置按需遍历而不预先创建整个区域列表 |
| EntityAction | `saturn:region_apply` | 对激活位置周围实体执行双实体动作，可选立方体、曼哈顿距离或球体 |
| BlockAction | `saturn:region_apply` | 对目标周围符合条件的方块执行动作，保留原有面方向与扩展上下文 |
| EntityAction | `saturn:area_of_effect` | 球形范围动作，复用 region_apply 的区域查询 |
| EntityAction | `saturn:selector_action` | 用原版实体选择器选择目标，再筛选并执行双实体动作，仅服务端有效 |
| EntityCondition | `saturn:enchantment` | 将全部装备槽的指定附魔等级求和或取最大值，再执行比较 |
| BlockCondition | `saturn:block_state` | 检查方块属性：布尔值、序列化枚举名或整数比较；缺失属性或不匹配的字段返回 false |
| BlockAction | `saturn:modify_block_state` | 循环、设置布尔或枚举属性，或加减/设置整数属性；无效值保持原状，仅服务端有效 |
| EntityAction | `saturn:ender_chest` | 为服务端玩家打开其末影箱 |
| EntityAction | `saturn:crafting_table` | 为服务端玩家打开不依赖实体工作台的合成界面 |
| ItemCondition | `saturn:smeltable` | 物品是否有熔炉熔炼配方，仅服务端有效 |
| EntityCondition | `saturn:in_snow` | 目标位置正在降雪 |
| EntityCondition | `saturn:in_thunderstorm` | 目标位置暴露于雷雨中 |
| BlockCondition | `saturn:in_snow` | 目标位置正在降雪 |
| BlockCondition | `saturn:in_thunderstorm` | 目标位置暴露于雷雨中 |
| BiEntityAction | `saturn:tame` | actor 为玩家且 target 可驯服时，将 target 驯服给 actor，仅服务端有效 |
| BiEntityAction | `saturn:set_in_love` | 令 target 动物进入繁殖状态；actor 为玩家时记录繁殖玩家，仅服务端有效 |
| EntityCondition | `saturn:player_ability` | 检查原版玩家能力标记，包括飞行、无限物品、无敌和建造权限；与 Saturn Ability 技能不同 |

## 复用已有类型

| 范围 | Origins 类型 | Saturn 对应 | 差异 |
| --- | --- | --- | --- |
| 动作组合 | `and` | `sequence` | 动作数组同样顺序执行。 |
| 实体动作 | `add_xp` | `add_experience` | 分别设置 points、levels。 |
| 实体动作 | `fire_projectile` | `spawn_projectile` | 保留 Saturn 的字段与激活上下文。 |
| 实体动作 | `block_action_at` | `block_action` | 在激活位置执行方块动作，嵌套字段为 action。 |
| 双实体动作 | `source_action` | `actor_action` | 对 actor 执行实体动作。 |
| 方块动作 | `add_block` | `set_block` | 使用 Saturn 的方块状态定义。 |
| 方块动作 | `destroy` | `break_block` | 复用原有破坏方块行为。 |
| 方块动作 | `absolute_offset` | `offset` | 使用 x、y、z 偏移。 |
| 物品动作 | `consume` | `consume_item` | 字段为 count，不是 amount。 |
| 物品动作 | `damage` | `damage_item` | 沿用现有耐久逻辑。 |
| 物品动作 | `merge_component` | `merge_components` | 沿用 Saturn 的组件补丁。 |
| 实体条件 | `in_tag` | `entity_tag` | 方块与物品上下文分别为 block_tag、item_tag。 |
| 实体条件 | `xp_levels` / `xp_points` | `experience_level` / `experience_points` | 沿用 comparison 与 compare_to。 |
| 实体条件 | `biome_in` | `on_block` + `biome_tag` | 按需要组合方块与群系条件，不新增 BiomeCondition 注册表。 |
| 实体条件 | `ability` | `player_ability` | 区分原版玩家标记与 Saturn Ability 技能。 |
| 伤害条件 | `id` / `type` | `damage_type` | 字段统一为 damage_type。 |
| 伤害条件 | `in_tag` | `damage_type_tag` | 沿用现有伤害类型标签。 |
| 延迟 | `delay` | timeline 的 `idle` / timeline `idle` | 使用现有时间线调度，不再建立全局 Timeout 队列。 |

## 适配差异

- 使用 Saturn 现有的四种 Action、五种 Condition 及 `MapCodec` 分派注册表，类型命名空间为 `saturn`。不新增一套动作执行器。
- 数值使用 `NumberProvider`，比较沿用 `comparison` / `compare_to`。嵌套动作与条件继续携带父上下文的公式、激活来源与扩展数据。
- `side` 判断逻辑端，单人游戏内置服务器按服务端处理。命令、进度、统计、菜单、原版 predicate 与背包写操作使用服务端 API；没有新增客户端类引用或 accessor/mixin。
- `for_range` 与 `while` 通过 `max_iterations` 限制每次调用，默认 1024。`for_range` 截断次数；`while` 仍满足条件且达到上限时抛出异常中断，不回滚已执行动作。
- 背包使用原版命令槽位 ID / 名称 / 范围，选中快捷栏和主手别名去重；只处理原版槽位，不读取 Power 专属背包。`modify_inventory` 按槽位执行一次，不保留 Origins 的 process_mode 迭代语义。
- 范围形状按相对中心计算，修正原实现的立方体轴差符号和实体绝对坐标距离问题。方块位置惰性遍历；`attachable` 使用相邻方块的真实坐标检查支撑面。
- `grant_advancement` / `revoke_advancement` 的 `selection: "everything"` 处理全部进度，不局限于给定进度所在树。该模式仍要求 advancement 字段，但不使用它。
- `fluid_height` 的 `fluid` 引用 NeoForge FluidType，例如 `minecraft:water`；`submerged_in` 接收流体 ID，再按其 FluidType 检查眼睛位置。

这是能力移植，不是 Origins 数据包的直接兼容层。即使类型 ID 相同，也应按对应 Saturn 类型页调整字段。

## 本次未移植

| 范围 | 原因或替代方式 |
| --- | --- |
| Power、Origin、entity set 与 ResourceReference 相关类型 | 依赖 Origins 的运行时状态；Saturn 已有独立 Ability、Resource、DataStorage 接口，不复制这些内部对象。 |
| `elytra_flight_possible` | 依赖 Caelus；没有引入额外必装依赖。 |
| `using_effective_tool` | 原实现直接访问客户端挖掘状态和游戏模式内部字段，需要额外的双端隔离与状态契约。 |
| `modify_death_ticks`、`modify_attack_cooldown` | 依赖内部字段访问或 accessor，不为这些动作新增 mixin。 |
| `variable_execute_command` | 原实现基于 Origins 的 ResourceReference；普通命令使用 execute_command。 |
| Loot modifier 物品 `modify` | 当前 ItemActionContext 只提供可原地修改的 ItemStack，不能可靠回写替换后的物品类型。 |
| `drop_inventory`、`replace_entity`、`random_teleport`、`raycast`、实体 `explode` | 原实现还涉及物品回写、实体复制、碰撞或射线行为，未作为本次直接移植类型；范围行为可使用 region_apply，方块上下文已有 explode。 |
| `moving` | 原实现依赖 MovingEntity 注入状态，本次不新增运动状态 mixin。 |
| `distance_from_coordinates` | 出生点、跨维度缩放与精度约定需要单独适配，本次未新增。 |

## 来源与验证范围

移植来源为 [Origins-NeoForge](https://github.com/IAFEnvoy/Origins-NeoForge) 的 `data/action/builtin`、`data/condition/builtin` 和相关工具类。Saturn 对其进行了上下文、Codec 与 Minecraft 26.1.2 API 适配；Saturn 不需要安装 Origins。

1.3.1 为本次本地源码与构建版本，文档更新不代表远端已经发布。按仓库约定只做编译、打包和文档构建检查，不启动 Minecraft，也不将编译通过表述为游戏行为已验收。
