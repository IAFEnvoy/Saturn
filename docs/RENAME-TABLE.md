# 逐文件改名对照表

Saturn 当前 **547** 个 Java 文件，其中 498 个在 MiXianTu 有同名来源、其余为 Saturn 新写。

- 包名 `com.iafenvoy.mxt` → `com.iafenvoy.saturn`；类前缀 `Mxt*` → `Saturn*`；`MiXianTu` → `Saturn`。
- 字符串 `mxt:` → `saturn:`、`mxt.` → `saturn.`、文件名 `Mxt*` → `Saturn*`。

| Saturn 路径 | 来源（MiXianTu） |
|---|---|
| `com/iafenvoy/saturn/advancement/IdentifierCriterionTrigger` | `src/main/java/com/iafenvoy/mxt/advancement/IdentifierCriterionTrigger.java` |
| `com/iafenvoy/saturn/api/AbilityHooks` | —（Saturn 新写） |
| `com/iafenvoy/saturn/api/AliasManager` | —（Saturn 新写） |
| `com/iafenvoy/saturn/api/ItemUseCycle` | `src/main/java/com/iafenvoy/mxt/api/ItemUseCycle.java` |
| `com/iafenvoy/saturn/api/package-info` | `src/main/java/com/iafenvoy/mxt/api/package-info.java` |
| `com/iafenvoy/saturn/api/PlayerNameQuery` | —（Saturn 新写） |
| `com/iafenvoy/saturn/attachment/AbilityAttachment` | `src/main/java/com/iafenvoy/mxt/attachment/AbilityAttachment.java` |
| `com/iafenvoy/saturn/attachment/ResourceHolderAttachment` | `src/main/java/com/iafenvoy/mxt/attachment/ResourceHolderAttachment.java` |
| `com/iafenvoy/saturn/attachment/TriggerCooldownAttachment` | `src/main/java/com/iafenvoy/mxt/attachment/TriggerCooldownAttachment.java` |
| `com/iafenvoy/saturn/command/client/SaturnClientCommands` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/client/SaturnHudCommand` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/SaturnAbilityCommand` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/SaturnCommands` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/SaturnDebugCommand` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/SaturnResourceCommand` | —（Saturn 新写） |
| `com/iafenvoy/saturn/command/SaturnTriggersCommand` | —（Saturn 新写） |
| `com/iafenvoy/saturn/compat/kubejs/binding/SaturnKubeJsActionBindings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/binding/MxtKubeJsActionBindings.java` |
| `com/iafenvoy/saturn/compat/kubejs/binding/SaturnKubeJsConditionBindings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/binding/MxtKubeJsConditionBindings.java` |
| `com/iafenvoy/saturn/compat/kubejs/binding/SaturnKubeJsCostBindings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/binding/MxtKubeJsCostBindings.java` |
| `com/iafenvoy/saturn/compat/kubejs/binding/SaturnKubeJsResourceBindings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/binding/MxtKubeJsResourceBindings.java` |
| `com/iafenvoy/saturn/compat/kubejs/binding/SaturnKubeJsValueBindings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/binding/MxtKubeJsValueBindings.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsActionCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsActionCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsConditionCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsConditionCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsCostCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsCostCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsSelectorCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsSelectorCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsTriggerMatchers` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsTriggerMatchers.java` |
| `com/iafenvoy/saturn/compat/kubejs/callback/SaturnJsValueCallbacks` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/callback/MxtJsValueCallbacks.java` |
| `com/iafenvoy/saturn/compat/kubejs/codec/SaturnJsCodecs` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/codec/MxtJsCodecs.java` |
| `com/iafenvoy/saturn/compat/kubejs/codec/SaturnKubeJsDataCodec` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/codec/MxtKubeJsDataCodec.java` |
| `com/iafenvoy/saturn/compat/kubejs/SaturnJsWarnings` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/MxtJsWarnings.java` |
| `com/iafenvoy/saturn/compat/kubejs/SaturnKubeJsApi` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/MxtKubeJsApi.java` |
| `com/iafenvoy/saturn/compat/kubejs/SaturnKubeJsPlugin` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/MxtKubeJsPlugin.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/action/JsBiEntityAction` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/action/JsBiEntityAction.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/action/JsBlockAction` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/action/JsBlockAction.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/action/JsEntityAction` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/action/JsEntityAction.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/action/JsItemAction` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/action/JsItemAction.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/condition/JsBiEntityCondition` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/condition/JsBiEntityCondition.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/condition/JsBlockCondition` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/condition/JsBlockCondition.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/condition/JsDamageCondition` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/condition/JsDamageCondition.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/condition/JsEntityCondition` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/condition/JsEntityCondition.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/condition/JsItemCondition` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/condition/JsItemCondition.java` |
| `com/iafenvoy/saturn/compat/kubejs/type/number/JsNumberProvider` | `src/main/java/com/iafenvoy/mxt/compat/kubejs/type/number/JsNumberProvider.java` |
| `com/iafenvoy/saturn/config/SaturnClientConfig` | `src/main/java/com/iafenvoy/mxt/config/MxtClientConfig.java` |
| `com/iafenvoy/saturn/data/ability/Ability` | `src/main/java/com/iafenvoy/mxt/data/ability/Ability.java` |
| `com/iafenvoy/saturn/data/ability/AbilityApplier` | `src/main/java/com/iafenvoy/mxt/data/ability/AbilityApplier.java` |
| `com/iafenvoy/saturn/data/ability/AbilityContext` | `src/main/java/com/iafenvoy/mxt/data/ability/AbilityContext.java` |
| `com/iafenvoy/saturn/data/ability/AbilityEffect` | `src/main/java/com/iafenvoy/mxt/data/ability/AbilityEffect.java` |
| `com/iafenvoy/saturn/data/ability/AbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/AbilityType.java` |
| `com/iafenvoy/saturn/data/ability/ActionCarrier` | `src/main/java/com/iafenvoy/mxt/data/ability/ActionCarrier.java` |
| `com/iafenvoy/saturn/data/ability/ChannelSource` | `src/main/java/com/iafenvoy/mxt/data/ability/ChannelSource.java` |
| `com/iafenvoy/saturn/data/ability/CooldownSource` | `src/main/java/com/iafenvoy/mxt/data/ability/CooldownSource.java` |
| `com/iafenvoy/saturn/data/ability/target/AreaTargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/target/AreaTargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/target/ConeTargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/target/ConeTargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/target/JsTargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/target/JsTargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/target/RayTargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/target/RayTargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/target/SelfTargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/target/SelfTargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/target/TargetGeometry` | `src/main/java/com/iafenvoy/mxt/data/ability/target/TargetGeometry.java` |
| `com/iafenvoy/saturn/data/ability/target/TargetOrder` | `src/main/java/com/iafenvoy/mxt/data/ability/target/TargetOrder.java` |
| `com/iafenvoy/saturn/data/ability/TargetSelector` | `src/main/java/com/iafenvoy/mxt/data/ability/TargetSelector.java` |
| `com/iafenvoy/saturn/data/ability/Togglable` | `src/main/java/com/iafenvoy/mxt/data/ability/Togglable.java` |
| `com/iafenvoy/saturn/data/ability/ToggleContext` | `src/main/java/com/iafenvoy/mxt/data/ability/ToggleContext.java` |
| `com/iafenvoy/saturn/data/ability/TriggerSource` | `src/main/java/com/iafenvoy/mxt/data/ability/TriggerSource.java` |
| `com/iafenvoy/saturn/data/ability/type/ActiveAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/ActiveAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/AuraAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/AuraAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/ChannelledAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/ChannelledAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/EmptyAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/EmptyAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/IntervalAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/IntervalAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/ModifierAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/ModifierAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/TargetedAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/TargetedAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/TriggeredAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/TriggeredAbilityType.java` |
| `com/iafenvoy/saturn/data/ability/type/UpkeepAbilityType` | `src/main/java/com/iafenvoy/mxt/data/ability/type/UpkeepAbilityType.java` |
| `com/iafenvoy/saturn/data/action/BiEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/BiEntityAction.java` |
| `com/iafenvoy/saturn/data/action/BlockAction` | `src/main/java/com/iafenvoy/mxt/data/action/BlockAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/AddVelocityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/AddVelocityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/HealTargetBiEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/HealTargetBiEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/ActorAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/ActorAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/ChanceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/ChanceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/ChoiceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/ChoiceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/IfElseAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/IfElseAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/SequenceBiEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/SequenceBiEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/meta/TargetAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/meta/TargetAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/TeleportAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/TeleportAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/bientity/TransferResourceBiEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/bientity/TransferResourceBiEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/BonemealAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/BonemealAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/BreakBlockAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/BreakBlockAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/ExplodeAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/ExplodeAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/LightUpAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/LightUpAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/meta/ChanceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/meta/ChanceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/meta/ChoiceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/meta/ChoiceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/meta/IfElseAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/meta/IfElseAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/meta/OffsetAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/meta/OffsetAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/meta/SequenceBlockAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/meta/SequenceBlockAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/ScheduleTickAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/ScheduleTickAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/SetBlockAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/SetBlockAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/block/SpawnEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/block/SpawnEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/AddExperienceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/AddExperienceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/AddResourceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/AddResourceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/AddVelocityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/AddVelocityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/ApplyEffectAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/ApplyEffectAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/BlockActionAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/BlockActionAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/EmitGameEventAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/EmitGameEventAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/EquippedItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/EquippedItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/ExhaustAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/ExhaustAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/FeedAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/FeedAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/GainAirAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/GainAirAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/GiveItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/GiveItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/GrantAbilityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/GrantAbilityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/HealAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/HealAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/KnockbackAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/KnockbackAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/meta/ChanceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/meta/ChanceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/meta/ChoiceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/meta/ChoiceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/meta/IfElseAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/meta/IfElseAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/meta/SequenceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/meta/SequenceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/ModifyAbilityStorageAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/ModifyAbilityStorageAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/PassengerAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/PassengerAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/PlaySoundAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/PlaySoundAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/RemoveAbilityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/RemoveAbilityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/RemoveEffectAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/RemoveEffectAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/RidingAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/RidingAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SelfBiEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SelfBiEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SetFallDistanceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SetFallDistanceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SetNoGravityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SetNoGravityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SetOnFireAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SetOnFireAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SpawnEffectCloudAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SpawnEffectCloudAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SpawnEntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SpawnEntityAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SpawnParticlesAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SpawnParticlesAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SpawnProjectileAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SpawnProjectileAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/SwingHandAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/SwingHandAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/entity/TeleportAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/entity/TeleportAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/AddEnchantmentAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/AddEnchantmentAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/ConsumeHealthItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/ConsumeHealthItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/ConsumeItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/ConsumeItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/CooldownAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/CooldownAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/DamageItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/DamageItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/MergeComponentsAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/MergeComponentsAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/meta/ChanceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/meta/ChanceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/meta/ChoiceAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/meta/ChoiceAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/meta/IfElseAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/meta/IfElseAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/meta/SequenceItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/meta/SequenceItemAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/item/RemoveEnchantmentAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/item/RemoveEnchantmentAction.java` |
| `com/iafenvoy/saturn/data/action/builtin/SendMessageAction` | `src/main/java/com/iafenvoy/mxt/data/action/builtin/SendMessageAction.java` |
| `com/iafenvoy/saturn/data/action/EntityAction` | `src/main/java/com/iafenvoy/mxt/data/action/EntityAction.java` |
| `com/iafenvoy/saturn/data/action/ItemAction` | `src/main/java/com/iafenvoy/mxt/data/action/ItemAction.java` |
| `com/iafenvoy/saturn/data/action/NoOpAction` | `src/main/java/com/iafenvoy/mxt/data/action/NoOpAction.java` |
| `com/iafenvoy/saturn/data/action/SimpleActions` | `src/main/java/com/iafenvoy/mxt/data/action/SimpleActions.java` |
| `com/iafenvoy/saturn/data/AttributeEntry` | `src/main/java/com/iafenvoy/mxt/data/AttributeEntry.java` |
| `com/iafenvoy/saturn/data/condition/AlwaysCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/AlwaysCondition.java` |
| `com/iafenvoy/saturn/data/condition/BiEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/BiEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/BlockCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/BlockCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/CanSeeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/CanSeeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/DistanceBiEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/DistanceBiEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/ActorCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/ActorCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/AndBiEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/AndBiEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/BothCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/BothCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/ChanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/ChanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/EitherCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/EitherCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/MutualCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/MutualCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/NotCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/NotCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/OrCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/OrCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/TargetCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/TargetCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/meta/UndirectedCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/meta/UndirectedCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/RelationBiEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/RelationBiEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/RelativeRotationCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/RelativeRotationCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/RidingRecursiveCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/RidingRecursiveCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/SameTeamCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/SameTeamCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/bientity/TeamBiEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/bientity/TeamBiEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/AdjacentCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/AdjacentCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/BiomeTagBlockCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/BiomeTagBlockCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/BlastResistanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/BlastResistanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/BlockIdCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/BlockIdCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/BlockTagCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/BlockTagCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/HardnessCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/HardnessCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/HeightCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/HeightCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/LightLevelCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/LightLevelCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/meta/AndBlockCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/meta/AndBlockCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/meta/ChanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/meta/ChanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/meta/NotCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/meta/NotCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/meta/OffsetCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/meta/OffsetCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/meta/OrCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/meta/OrCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/MovementBlockingCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/MovementBlockingCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/block/SlipperinessCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/block/SlipperinessCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/DamageAmountRangeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/DamageAmountRangeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/DamageTypeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/DamageTypeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/DamageTypeTagCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/DamageTypeTagCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/DirectDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/DirectDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/FireDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/FireDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/MagicDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/MagicDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/meta/AndDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/meta/AndDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/meta/ChanceDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/meta/ChanceDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/meta/NotDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/meta/NotDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/meta/OrDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/meta/OrDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/damage/ProjectileDamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/damage/ProjectileDamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageChargesCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageChargesCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageCooldownCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageCooldownCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageReading` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageReading.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageResourceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageResourceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageTargetCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageTargetCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageTimerCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageTimerCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AbilityStorageToggleCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AbilityStorageToggleCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AirCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AirCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AttackCooldownCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AttackCooldownCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/AttributeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/AttributeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/BlockCollisionCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/BlockCollisionCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/BrightnessCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/BrightnessCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/CanHaveEffectCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/CanHaveEffectCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/DimensionCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/DimensionCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/EntityTypeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/EntityTypeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/EntityTypeTagCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/EntityTypeTagCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/EquippedItemCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/EquippedItemCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ExperienceLevelCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ExperienceLevelCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ExperiencePointsCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ExperiencePointsCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ExposedToSkyCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ExposedToSkyCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ExposedToSunCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ExposedToSunCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/FallDistanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/FallDistanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/FoodLevelCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/FoodLevelCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/GamemodeCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/GamemodeCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/GlowingCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/GlowingCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/HasAbilityEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/HasAbilityEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/HealthCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/HealthCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/InBlockAnywhereCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/InBlockAnywhereCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/InBlockCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/InBlockCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/MainHandItemCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/MainHandItemCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/meta/AndEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/meta/AndEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/meta/ChanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/meta/ChanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/meta/NotEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/meta/NotEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/meta/OrEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/meta/OrEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/MobEffectCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/MobEffectCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/OnBlockCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/OnBlockCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/PartnerEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/PartnerEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/PassengerCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/PassengerCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/PassengerRecursiveCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/PassengerRecursiveCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/RelativeHealthCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/RelativeHealthCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ResourceCompareEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ResourceCompareEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/RidingCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/RidingCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/RidingRecursiveCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/RidingRecursiveCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/RidingRootCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/RidingRootCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/SaturationLevelCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/SaturationLevelCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/ScoreboardCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/ScoreboardCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/SneakingEntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/SneakingEntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/TeamCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/TeamCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/TimeOfDayCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/TimeOfDayCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/entity/UsingItemCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/entity/UsingItemCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/AmountCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/AmountCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ArmorValueCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ArmorValueCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/BaseEnchantmentCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/BaseEnchantmentCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ComponentCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ComponentCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/DurabilityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/DurabilityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/FuelCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/FuelCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/HasComponentCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/HasComponentCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/IngredientCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/IngredientCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/IsEquipableCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/IsEquipableCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ItemIdCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ItemIdCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ItemMatcherCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ItemMatcherCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ItemTagCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ItemTagCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/meta/AndItemCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/meta/AndItemCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/meta/ChanceCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/meta/ChanceCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/meta/NotCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/meta/NotCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/meta/OrCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/meta/OrCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/OnCooldownCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/OnCooldownCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/RelativeDurabilityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/RelativeDurabilityCondition.java` |
| `com/iafenvoy/saturn/data/condition/builtin/item/ToolAbilityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/builtin/item/ToolAbilityCondition.java` |
| `com/iafenvoy/saturn/data/condition/DamageCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/DamageCondition.java` |
| `com/iafenvoy/saturn/data/condition/EntityCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/EntityCondition.java` |
| `com/iafenvoy/saturn/data/condition/ItemCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/ItemCondition.java` |
| `com/iafenvoy/saturn/data/condition/NeverCondition` | `src/main/java/com/iafenvoy/mxt/data/condition/NeverCondition.java` |
| `com/iafenvoy/saturn/data/condition/SimpleConditions` | `src/main/java/com/iafenvoy/mxt/data/condition/SimpleConditions.java` |
| `com/iafenvoy/saturn/data/context/action/BiEntityActionContext` | `src/main/java/com/iafenvoy/mxt/data/context/action/BiEntityActionContext.java` |
| `com/iafenvoy/saturn/data/context/action/BlockActionContext` | `src/main/java/com/iafenvoy/mxt/data/context/action/BlockActionContext.java` |
| `com/iafenvoy/saturn/data/context/action/EntityActionContext` | `src/main/java/com/iafenvoy/mxt/data/context/action/EntityActionContext.java` |
| `com/iafenvoy/saturn/data/context/action/ItemActionContext` | `src/main/java/com/iafenvoy/mxt/data/context/action/ItemActionContext.java` |
| `com/iafenvoy/saturn/data/context/condition/BiEntityConditionContext` | `src/main/java/com/iafenvoy/mxt/data/context/condition/BiEntityConditionContext.java` |
| `com/iafenvoy/saturn/data/context/condition/BlockConditionContext` | `src/main/java/com/iafenvoy/mxt/data/context/condition/BlockConditionContext.java` |
| `com/iafenvoy/saturn/data/context/condition/DamageConditionContext` | `src/main/java/com/iafenvoy/mxt/data/context/condition/DamageConditionContext.java` |
| `com/iafenvoy/saturn/data/context/condition/EntityConditionContext` | `src/main/java/com/iafenvoy/mxt/data/context/condition/EntityConditionContext.java` |
| `com/iafenvoy/saturn/data/context/condition/ItemConditionContext` | `src/main/java/com/iafenvoy/mxt/data/context/condition/ItemConditionContext.java` |
| `com/iafenvoy/saturn/data/context/Context` | `src/main/java/com/iafenvoy/mxt/data/context/Context.java` |
| `com/iafenvoy/saturn/data/cost/builtin/ItemCost` | `src/main/java/com/iafenvoy/mxt/data/cost/builtin/ItemCost.java` |
| `com/iafenvoy/saturn/data/cost/builtin/JsCost` | `src/main/java/com/iafenvoy/mxt/data/cost/builtin/JsCost.java` |
| `com/iafenvoy/saturn/data/cost/builtin/ResourceChannel` | `src/main/java/com/iafenvoy/mxt/data/cost/builtin/ResourceChannel.java` |
| `com/iafenvoy/saturn/data/cost/builtin/ResourceCost` | `src/main/java/com/iafenvoy/mxt/data/cost/builtin/ResourceCost.java` |
| `com/iafenvoy/saturn/data/cost/context/CostContext` | `src/main/java/com/iafenvoy/mxt/data/cost/context/CostContext.java` |
| `com/iafenvoy/saturn/data/cost/context/CostOrigins` | `src/main/java/com/iafenvoy/mxt/data/cost/context/CostOrigins.java` |
| `com/iafenvoy/saturn/data/cost/Cost` | `src/main/java/com/iafenvoy/mxt/data/cost/Cost.java` |
| `com/iafenvoy/saturn/data/cost/CostPayment` | `src/main/java/com/iafenvoy/mxt/data/cost/CostPayment.java` |
| `com/iafenvoy/saturn/data/cost/CostTarget` | `src/main/java/com/iafenvoy/mxt/data/cost/CostTarget.java` |
| `com/iafenvoy/saturn/data/cost/CostTransaction` | `src/main/java/com/iafenvoy/mxt/data/cost/CostTransaction.java` |
| `com/iafenvoy/saturn/data/cost/Debit` | `src/main/java/com/iafenvoy/mxt/data/cost/Debit.java` |
| `com/iafenvoy/saturn/data/DescribedEntry` | `src/main/java/com/iafenvoy/mxt/data/DescribedEntry.java` |
| `com/iafenvoy/saturn/data/IconReference` | `src/main/java/com/iafenvoy/mxt/data/IconReference.java` |
| `com/iafenvoy/saturn/data/item/IncompleteMarker` | `src/main/java/com/iafenvoy/mxt/data/item/IncompleteMarker.java` |
| `com/iafenvoy/saturn/data/MessageChannel` | `src/main/java/com/iafenvoy/mxt/data/MessageChannel.java` |
| `com/iafenvoy/saturn/data/ParticleEffect` | `src/main/java/com/iafenvoy/mxt/data/ParticleEffect.java` |
| `com/iafenvoy/saturn/data/resource/builtin/ConstantResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/builtin/ConstantResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resource/builtin/CurrentResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/builtin/CurrentResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resource/builtin/JsResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/builtin/JsResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resource/builtin/MaximumResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/builtin/MaximumResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resource/builtin/MissingResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/builtin/MissingResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resource/Resource` | `src/main/java/com/iafenvoy/mxt/data/resource/Resource.java` |
| `com/iafenvoy/saturn/data/resource/ResourceBar` | `src/main/java/com/iafenvoy/mxt/data/resource/ResourceBar.java` |
| `com/iafenvoy/saturn/data/resource/ResourceValueProvider` | `src/main/java/com/iafenvoy/mxt/data/resource/ResourceValueProvider.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/context/BossOverlayContext` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/context/BossOverlayContext.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/context/SelfHudContext` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/context/SelfHudContext.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/context/TargetOverlayContext` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/context/TargetOverlayContext.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/MissingRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/MissingRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/OriginsRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/OriginsRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/RadialRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/RadialRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/SegmentedRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/SegmentedRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/TextOnlyRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/TextOnlyRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/renderdata/TexturedRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/renderdata/TexturedRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/AlwaysVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/AlwaysVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/AndVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/AndVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/NonFullVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/NonFullVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/NonZeroVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/NonZeroVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/NotVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/NotVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/OrVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/OrVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/RangeVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/RangeVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/builtin/visibility/RecentlyChangedVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/builtin/visibility/RecentlyChangedVisibility.java` |
| `com/iafenvoy/saturn/data/resourcebar/ResourceBarContext` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/ResourceBarContext.java` |
| `com/iafenvoy/saturn/data/resourcebar/ResourceBarRenderData` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/ResourceBarRenderData.java` |
| `com/iafenvoy/saturn/data/resourcebar/ResourceBarView` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/ResourceBarView.java` |
| `com/iafenvoy/saturn/data/resourcebar/ResourceBarVisibility` | `src/main/java/com/iafenvoy/mxt/data/resourcebar/ResourceBarVisibility.java` |
| `com/iafenvoy/saturn/data/SpriteIcon` | `src/main/java/com/iafenvoy/mxt/data/SpriteIcon.java` |
| `com/iafenvoy/saturn/data/storage/builtin/ChargesDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/ChargesDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/ContainerDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/ContainerDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/CooldownDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/CooldownDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/EmptyDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/EmptyDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/ResourceDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/ResourceDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/TargetLockDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/TargetLockDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/TimerDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/TimerDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/builtin/ToggleDataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/builtin/ToggleDataStorage.java` |
| `com/iafenvoy/saturn/data/storage/DataStorage` | `src/main/java/com/iafenvoy/mxt/data/storage/DataStorage.java` |
| `com/iafenvoy/saturn/data/storage/DataStorageCollector` | `src/main/java/com/iafenvoy/mxt/data/storage/DataStorageCollector.java` |
| `com/iafenvoy/saturn/data/storage/DataStorageDeclaration` | `src/main/java/com/iafenvoy/mxt/data/storage/DataStorageDeclaration.java` |
| `com/iafenvoy/saturn/data/storage/DataStorageHolder` | `src/main/java/com/iafenvoy/mxt/data/storage/DataStorageHolder.java` |
| `com/iafenvoy/saturn/data/storage/runtime/ActiveState` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/ActiveState.java` |
| `com/iafenvoy/saturn/data/storage/runtime/AuraPulse` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/AuraPulse.java` |
| `com/iafenvoy/saturn/data/storage/runtime/CastDeadline` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/CastDeadline.java` |
| `com/iafenvoy/saturn/data/storage/runtime/ChannelPulse` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/ChannelPulse.java` |
| `com/iafenvoy/saturn/data/storage/runtime/EntryBegan` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/EntryBegan.java` |
| `com/iafenvoy/saturn/data/storage/runtime/IdleCountdown` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/IdleCountdown.java` |
| `com/iafenvoy/saturn/data/storage/runtime/WaitCountdown` | `src/main/java/com/iafenvoy/mxt/data/storage/runtime/WaitCountdown.java` |
| `com/iafenvoy/saturn/data/timeline/builtin/ActionEntry` | `src/main/java/com/iafenvoy/mxt/data/timeline/builtin/ActionEntry.java` |
| `com/iafenvoy/saturn/data/timeline/builtin/BranchEntry` | `src/main/java/com/iafenvoy/mxt/data/timeline/builtin/BranchEntry.java` |
| `com/iafenvoy/saturn/data/timeline/builtin/IdleEntry` | `src/main/java/com/iafenvoy/mxt/data/timeline/builtin/IdleEntry.java` |
| `com/iafenvoy/saturn/data/timeline/builtin/WaitForEntry` | `src/main/java/com/iafenvoy/mxt/data/timeline/builtin/WaitForEntry.java` |
| `com/iafenvoy/saturn/data/timeline/TimelineContext` | `src/main/java/com/iafenvoy/mxt/data/timeline/TimelineContext.java` |
| `com/iafenvoy/saturn/data/timeline/TimelineEntry` | `src/main/java/com/iafenvoy/mxt/data/timeline/TimelineEntry.java` |
| `com/iafenvoy/saturn/data/timeline/TimelineJump` | `src/main/java/com/iafenvoy/mxt/data/timeline/TimelineJump.java` |
| `com/iafenvoy/saturn/data/timeline/TimelineState` | `src/main/java/com/iafenvoy/mxt/data/timeline/TimelineState.java` |
| `com/iafenvoy/saturn/data/trigger/builtin/BuiltinTrigger` | `src/main/java/com/iafenvoy/mxt/data/trigger/builtin/BuiltinTrigger.java` |
| `com/iafenvoy/saturn/data/trigger/builtin/JsTrigger` | `src/main/java/com/iafenvoy/mxt/data/trigger/builtin/JsTrigger.java` |
| `com/iafenvoy/saturn/data/trigger/builtin/VanillaTrigger` | `src/main/java/com/iafenvoy/mxt/data/trigger/builtin/VanillaTrigger.java` |
| `com/iafenvoy/saturn/data/trigger/builtin/VanillaTriggerMatchers` | `src/main/java/com/iafenvoy/mxt/data/trigger/builtin/VanillaTriggerMatchers.java` |
| `com/iafenvoy/saturn/data/trigger/builtin/VanillaTriggerSupport` | `src/main/java/com/iafenvoy/mxt/data/trigger/builtin/VanillaTriggerSupport.java` |
| `com/iafenvoy/saturn/data/trigger/Trigger` | `src/main/java/com/iafenvoy/mxt/data/trigger/Trigger.java` |
| `com/iafenvoy/saturn/data/trigger/TriggerContext` | `src/main/java/com/iafenvoy/mxt/data/trigger/TriggerContext.java` |
| `com/iafenvoy/saturn/data/trigger/TriggerPayload` | `src/main/java/com/iafenvoy/mxt/data/trigger/TriggerPayload.java` |
| `com/iafenvoy/saturn/data/trigger/TriggerRule` | `src/main/java/com/iafenvoy/mxt/data/trigger/TriggerRule.java` |
| `com/iafenvoy/saturn/data/trigger/TriggerSignal` | `src/main/java/com/iafenvoy/mxt/data/trigger/TriggerSignal.java` |
| `com/iafenvoy/saturn/data/trigger/TriggerSignals` | `src/main/java/com/iafenvoy/mxt/data/trigger/TriggerSignals.java` |
| `com/iafenvoy/saturn/data/Weighted` | `src/main/java/com/iafenvoy/mxt/data/Weighted.java` |
| `com/iafenvoy/saturn/event/AbilityTriggeredEvent` | `src/main/java/com/iafenvoy/mxt/event/AbilityTriggeredEvent.java` |
| `com/iafenvoy/saturn/event/AbilityUseEvent` | `src/main/java/com/iafenvoy/mxt/event/AbilityUseEvent.java` |
| `com/iafenvoy/saturn/event/CollectAbilityGrantsEvent` | `src/main/java/com/iafenvoy/mxt/event/CollectAbilityGrantsEvent.java` |
| `com/iafenvoy/saturn/event/CollectPassiveAttributesEvent` | `src/main/java/com/iafenvoy/mxt/event/CollectPassiveAttributesEvent.java` |
| `com/iafenvoy/saturn/event/CollectTickStagesEvent` | `src/main/java/com/iafenvoy/mxt/event/CollectTickStagesEvent.java` |
| `com/iafenvoy/saturn/event/ResolveItemUseCycleEvent` | `src/main/java/com/iafenvoy/mxt/event/ResolveItemUseCycleEvent.java` |
| `com/iafenvoy/saturn/event/ResourceConsumeEvent` | `src/main/java/com/iafenvoy/mxt/event/ResourceConsumeEvent.java` |
| `com/iafenvoy/saturn/event/TriggerRehydrateEvent` | `src/main/java/com/iafenvoy/mxt/event/TriggerRehydrateEvent.java` |
| `com/iafenvoy/saturn/loot/GrantAbilityLootFunction` | `src/main/java/com/iafenvoy/mxt/loot/GrantAbilityLootFunction.java` |
| `com/iafenvoy/saturn/loot/HasAbilityLootCondition` | `src/main/java/com/iafenvoy/mxt/loot/HasAbilityLootCondition.java` |
| `com/iafenvoy/saturn/mixin/ItemUseCycleMixin` | `src/main/java/com/iafenvoy/mxt/mixin/ItemUseCycleMixin.java` |
| `com/iafenvoy/saturn/profiling/LoggerDispatcher` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/LoggingLevel` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/Profiler` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerAggregator` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerFrame` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerScope` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerSource` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerSources` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ProfilerWindowSnapshot` | —（Saturn 新写） |
| `com/iafenvoy/saturn/profiling/ServerProfiling` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/cache/ChainCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/ChainCache.java` |
| `com/iafenvoy/saturn/registry/cache/FormulaNameCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/FormulaNameCache.java` |
| `com/iafenvoy/saturn/registry/cache/ItemMatcherCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/ItemMatcherCache.java` |
| `com/iafenvoy/saturn/registry/cache/OrderCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/OrderCache.java` |
| `com/iafenvoy/saturn/registry/cache/RegistryCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/RegistryCache.java` |
| `com/iafenvoy/saturn/registry/cache/RegistryCaches` | `src/main/java/com/iafenvoy/mxt/registry/cache/RegistryCaches.java` |
| `com/iafenvoy/saturn/registry/cache/ReverseMappingCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/ReverseMappingCache.java` |
| `com/iafenvoy/saturn/registry/cache/ReverseMappingListCache` | `src/main/java/com/iafenvoy/mxt/registry/cache/ReverseMappingListCache.java` |
| `com/iafenvoy/saturn/registry/content/SaturnAbilityTypes` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnBiEntityActions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnBiEntityConditions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnBlockActions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnBlockConditions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnCosts` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnDamageConditions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnDataStorages` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnEntityActions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnEntityConditions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnFormulaFunctions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnFormulaVariables` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnItemActions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnItemConditions` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnItemMatchers` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnNumberProviders` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnResourceBarContexts` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnResourceBarRenderers` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnResourceBarVisibilities` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnResourceValueProviders` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnTargetSelectors` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnTimelineEntries` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/content/SaturnTriggers` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/SaturnAliases` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/SaturnAttachments` | `src/main/java/com/iafenvoy/mxt/registry/MxtAttachments.java` |
| `com/iafenvoy/saturn/registry/SaturnContent` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/SaturnDatapackRegistries` | `src/main/java/com/iafenvoy/mxt/registry/MxtDatapackRegistries.java` |
| `com/iafenvoy/saturn/registry/SaturnKeyMappings` | `src/main/java/com/iafenvoy/mxt/registry/MxtKeyMappings.java` |
| `com/iafenvoy/saturn/registry/SaturnLootRegistries` | —（Saturn 新写） |
| `com/iafenvoy/saturn/registry/SaturnRegistries` | `src/main/java/com/iafenvoy/mxt/registry/MxtRegistries.java` |
| `com/iafenvoy/saturn/registry/SaturnResourceKeys` | `src/main/java/com/iafenvoy/mxt/registry/MxtResourceKeys.java` |
| `com/iafenvoy/saturn/render/IconRenderer` | `src/main/java/com/iafenvoy/mxt/render/IconRenderer.java` |
| `com/iafenvoy/saturn/render/item/IncompleteMarkerRenderer` | `src/main/java/com/iafenvoy/mxt/render/item/IncompleteMarkerRenderer.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityActivationService` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityActivationService.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityEventBridge` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityEventBridge.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityFeedback` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityFeedback.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityGrantService` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityGrantService.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityModifierService` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityModifierService.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityService` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityService.java` |
| `com/iafenvoy/saturn/runtime/ability/AbilityStorage` | `src/main/java/com/iafenvoy/mxt/runtime/ability/AbilityStorage.java` |
| `com/iafenvoy/saturn/runtime/ability/PassiveAttributeService` | `src/main/java/com/iafenvoy/mxt/runtime/ability/PassiveAttributeService.java` |
| `com/iafenvoy/saturn/runtime/EntityTickStages` | `src/main/java/com/iafenvoy/mxt/runtime/EntityTickStages.java` |
| `com/iafenvoy/saturn/runtime/hold/ItemUseCycles` | `src/main/java/com/iafenvoy/mxt/runtime/hold/ItemUseCycles.java` |
| `com/iafenvoy/saturn/runtime/hold/ItemUseCyclesClient` | `src/main/java/com/iafenvoy/mxt/runtime/hold/ItemUseCyclesClient.java` |
| `com/iafenvoy/saturn/runtime/resource/ResourceService` | `src/main/java/com/iafenvoy/mxt/runtime/resource/ResourceService.java` |
| `com/iafenvoy/saturn/runtime/resource/ResourceTransactions` | `src/main/java/com/iafenvoy/mxt/runtime/resource/ResourceTransactions.java` |
| `com/iafenvoy/saturn/runtime/resulting/Failure` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/Failure.java` |
| `com/iafenvoy/saturn/runtime/resulting/Failures` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/Failures.java` |
| `com/iafenvoy/saturn/runtime/resulting/failures/AbilityFailure` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/failures/AbilityFailure.java` |
| `com/iafenvoy/saturn/runtime/resulting/failures/CostFailure` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/failures/CostFailure.java` |
| `com/iafenvoy/saturn/runtime/resulting/FailureSource` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/FailureSource.java` |
| `com/iafenvoy/saturn/runtime/resulting/FailureSources` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/FailureSources.java` |
| `com/iafenvoy/saturn/runtime/resulting/Result` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/Result.java` |
| `com/iafenvoy/saturn/runtime/resulting/ResultWithData` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/ResultWithData.java` |
| `com/iafenvoy/saturn/runtime/resulting/SimpleFailure` | `src/main/java/com/iafenvoy/mxt/runtime/resulting/SimpleFailure.java` |
| `com/iafenvoy/saturn/runtime/Sources` | `src/main/java/com/iafenvoy/mxt/runtime/Sources.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerDispatcher` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerDispatcher.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerLifecycleEvents` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerLifecycleEvents.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerPublishing` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerPublishing.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerRehydrators` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerRehydrators.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerRuleService` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerRuleService.java` |
| `com/iafenvoy/saturn/runtime/trigger/TriggerSubscription` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/TriggerSubscription.java` |
| `com/iafenvoy/saturn/runtime/trigger/VanillaTriggerBridge` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/VanillaTriggerBridge.java` |
| `com/iafenvoy/saturn/runtime/trigger/VanillaTriggerPollBridge` | `src/main/java/com/iafenvoy/mxt/runtime/trigger/VanillaTriggerPollBridge.java` |
| `com/iafenvoy/saturn/Saturn` | `src/main/java/com/iafenvoy/mxt/MiXianTu.java` |
| `com/iafenvoy/saturn/SaturnClient` | —（Saturn 新写） |
| `com/iafenvoy/saturn/screen/hud/AbstractHudEntry` | `src/main/java/com/iafenvoy/mxt/screen/hud/AbstractHudEntry.java` |
| `com/iafenvoy/saturn/screen/hud/HudAnchor` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudAnchor.java` |
| `com/iafenvoy/saturn/screen/hud/HudEditScreen` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudEditScreen.java` |
| `com/iafenvoy/saturn/screen/hud/HudEntry` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudEntry.java` |
| `com/iafenvoy/saturn/screen/hud/HudLayout` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudLayout.java` |
| `com/iafenvoy/saturn/screen/hud/HudLayoutFile` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudLayoutFile.java` |
| `com/iafenvoy/saturn/screen/hud/HudManager` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudManager.java` |
| `com/iafenvoy/saturn/screen/hud/HudPlacement` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudPlacement.java` |
| `com/iafenvoy/saturn/screen/hud/HudRenderer` | `src/main/java/com/iafenvoy/mxt/screen/hud/HudRenderer.java` |
| `com/iafenvoy/saturn/screen/hud/RenderBlock` | `src/main/java/com/iafenvoy/mxt/screen/hud/RenderBlock.java` |
| `com/iafenvoy/saturn/screen/hud/ScreenBounds` | `src/main/java/com/iafenvoy/mxt/screen/hud/ScreenBounds.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/MissingResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/MissingResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/OriginsResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/OriginsResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/RadialResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/RadialResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/ResourceBarRendererHelper` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/ResourceBarRendererHelper.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/SegmentedResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/SegmentedResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/TextOnlyResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/TextOnlyResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/renderer/TexturedResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/renderer/TexturedResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarBlock` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarBlock.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarEntry` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarEntry.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarFixedEntry` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarFixedEntry.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarOverlay` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarOverlay.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarRenderer` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarRenderer.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarRendererDispatcher` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarRendererDispatcher.java` |
| `com/iafenvoy/saturn/screen/resourcebar/ResourceBarRenderState` | `src/main/java/com/iafenvoy/mxt/screen/resourcebar/ResourceBarRenderState.java` |
| `com/iafenvoy/saturn/util/AbstractChangeTrackable` | `src/main/java/com/iafenvoy/mxt/util/AbstractChangeTrackable.java` |
| `com/iafenvoy/saturn/util/chain/Chain` | `src/main/java/com/iafenvoy/mxt/util/chain/Chain.java` |
| `com/iafenvoy/saturn/util/chain/ChainNode` | `src/main/java/com/iafenvoy/mxt/util/chain/ChainNode.java` |
| `com/iafenvoy/saturn/util/chain/ChainWalker` | `src/main/java/com/iafenvoy/mxt/util/chain/ChainWalker.java` |
| `com/iafenvoy/saturn/util/ChangeTrackable` | `src/main/java/com/iafenvoy/mxt/util/ChangeTrackable.java` |
| `com/iafenvoy/saturn/util/ClientLevelAccess` | `src/main/java/com/iafenvoy/mxt/util/ClientLevelAccess.java` |
| `com/iafenvoy/saturn/util/ClientPlayerNames` | `src/main/java/com/iafenvoy/mxt/util/ClientPlayerNames.java` |
| `com/iafenvoy/saturn/util/ClientRenderResources` | `src/main/java/com/iafenvoy/mxt/util/ClientRenderResources.java` |
| `com/iafenvoy/saturn/util/CloseableResources` | `src/main/java/com/iafenvoy/mxt/util/CloseableResources.java` |
| `com/iafenvoy/saturn/util/codec/CollectionCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/CollectionCodecs.java` |
| `com/iafenvoy/saturn/util/codec/ExtraEnumCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/ExtraEnumCodecs.java` |
| `com/iafenvoy/saturn/util/codec/MiscCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/MiscCodecs.java` |
| `com/iafenvoy/saturn/util/codec/MiscStreamCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/MiscStreamCodecs.java` |
| `com/iafenvoy/saturn/util/codec/OptionalCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/OptionalCodecs.java` |
| `com/iafenvoy/saturn/util/codec/RegistryCodecs` | `src/main/java/com/iafenvoy/mxt/util/codec/RegistryCodecs.java` |
| `com/iafenvoy/saturn/util/codec/schema/SchemaCodecs` | —（Saturn 新写） |
| `com/iafenvoy/saturn/util/codec/schema/SchemaMapCodec` | —（Saturn 新写） |
| `com/iafenvoy/saturn/util/codec/TolerantListCodec` | `src/main/java/com/iafenvoy/mxt/util/codec/TolerantListCodec.java` |
| `com/iafenvoy/saturn/util/codec/TolerantMapCodec` | `src/main/java/com/iafenvoy/mxt/util/codec/TolerantMapCodec.java` |
| `com/iafenvoy/saturn/util/DefinitionText` | `src/main/java/com/iafenvoy/mxt/util/DefinitionText.java` |
| `com/iafenvoy/saturn/util/dispatcher/TickDispatcher` | `src/main/java/com/iafenvoy/mxt/util/dispatcher/TickDispatcher.java` |
| `com/iafenvoy/saturn/util/dispatcher/TickExecutionLimit` | `src/main/java/com/iafenvoy/mxt/util/dispatcher/TickExecutionLimit.java` |
| `com/iafenvoy/saturn/util/dispatcher/TickInterval` | `src/main/java/com/iafenvoy/mxt/util/dispatcher/TickInterval.java` |
| `com/iafenvoy/saturn/util/formula/FormulaContext` | `src/main/java/com/iafenvoy/mxt/util/formula/FormulaContext.java` |
| `com/iafenvoy/saturn/util/formula/FormulaContexts` | `src/main/java/com/iafenvoy/mxt/util/formula/FormulaContexts.java` |
| `com/iafenvoy/saturn/util/formula/FormulaFunctions` | `src/main/java/com/iafenvoy/mxt/util/formula/FormulaFunctions.java` |
| `com/iafenvoy/saturn/util/formula/FormulaVariable` | `src/main/java/com/iafenvoy/mxt/util/formula/FormulaVariable.java` |
| `com/iafenvoy/saturn/util/formula/FormulaVariables` | `src/main/java/com/iafenvoy/mxt/util/formula/FormulaVariables.java` |
| `com/iafenvoy/saturn/util/formula/number/Binomial` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Binomial.java` |
| `com/iafenvoy/saturn/util/formula/number/Conditional` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Conditional.java` |
| `com/iafenvoy/saturn/util/formula/number/Constant` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Constant.java` |
| `com/iafenvoy/saturn/util/formula/number/ContextVariable` | `src/main/java/com/iafenvoy/mxt/util/formula/number/ContextVariable.java` |
| `com/iafenvoy/saturn/util/formula/number/Expression` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Expression.java` |
| `com/iafenvoy/saturn/util/formula/number/Sum` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Sum.java` |
| `com/iafenvoy/saturn/util/formula/number/Uniform` | `src/main/java/com/iafenvoy/mxt/util/formula/number/Uniform.java` |
| `com/iafenvoy/saturn/util/formula/number/WeightedList` | `src/main/java/com/iafenvoy/mxt/util/formula/number/WeightedList.java` |
| `com/iafenvoy/saturn/util/formula/NumberProvider` | `src/main/java/com/iafenvoy/mxt/util/formula/NumberProvider.java` |
| `com/iafenvoy/saturn/util/formula/NumberRange` | `src/main/java/com/iafenvoy/mxt/util/formula/NumberRange.java` |
| `com/iafenvoy/saturn/util/formula/SimpleFunction` | `src/main/java/com/iafenvoy/mxt/util/formula/SimpleFunction.java` |
| `com/iafenvoy/saturn/util/formula/variable/EntityVariable` | `src/main/java/com/iafenvoy/mxt/util/formula/variable/EntityVariable.java` |
| `com/iafenvoy/saturn/util/formula/variable/RandomVariable` | `src/main/java/com/iafenvoy/mxt/util/formula/variable/RandomVariable.java` |
| `com/iafenvoy/saturn/util/formula/variable/ZeroVariable` | `src/main/java/com/iafenvoy/mxt/util/formula/variable/ZeroVariable.java` |
| `com/iafenvoy/saturn/util/function/QuadConsumer` | `src/main/java/com/iafenvoy/mxt/util/function/QuadConsumer.java` |
| `com/iafenvoy/saturn/util/function/QuadFunction` | `src/main/java/com/iafenvoy/mxt/util/function/QuadFunction.java` |
| `com/iafenvoy/saturn/util/function/QuadPredicate` | `src/main/java/com/iafenvoy/mxt/util/function/QuadPredicate.java` |
| `com/iafenvoy/saturn/util/function/TriConsumer` | `src/main/java/com/iafenvoy/mxt/util/function/TriConsumer.java` |
| `com/iafenvoy/saturn/util/function/TriFunction` | `src/main/java/com/iafenvoy/mxt/util/function/TriFunction.java` |
| `com/iafenvoy/saturn/util/function/TriPredicate` | `src/main/java/com/iafenvoy/mxt/util/function/TriPredicate.java` |
| `com/iafenvoy/saturn/util/HolderHelper` | `src/main/java/com/iafenvoy/mxt/util/HolderHelper.java` |
| `com/iafenvoy/saturn/util/InventoryUtil` | `src/main/java/com/iafenvoy/mxt/util/InventoryUtil.java` |
| `com/iafenvoy/saturn/util/matcher/builtin/IngredientEntry` | `src/main/java/com/iafenvoy/mxt/util/matcher/builtin/IngredientEntry.java` |
| `com/iafenvoy/saturn/util/matcher/builtin/ItemEntry` | `src/main/java/com/iafenvoy/mxt/util/matcher/builtin/ItemEntry.java` |
| `com/iafenvoy/saturn/util/matcher/builtin/RegexEntry` | `src/main/java/com/iafenvoy/mxt/util/matcher/builtin/RegexEntry.java` |
| `com/iafenvoy/saturn/util/matcher/builtin/TagEntry` | `src/main/java/com/iafenvoy/mxt/util/matcher/builtin/TagEntry.java` |
| `com/iafenvoy/saturn/util/matcher/builtin/WildcardEntry` | `src/main/java/com/iafenvoy/mxt/util/matcher/builtin/WildcardEntry.java` |
| `com/iafenvoy/saturn/util/matcher/ItemMatcher` | `src/main/java/com/iafenvoy/mxt/util/matcher/ItemMatcher.java` |
| `com/iafenvoy/saturn/util/math/CompareOperation` | `src/main/java/com/iafenvoy/mxt/util/math/CompareOperation.java` |
| `com/iafenvoy/saturn/util/math/Comparison` | `src/main/java/com/iafenvoy/mxt/util/math/Comparison.java` |
| `com/iafenvoy/saturn/util/math/Space` | `src/main/java/com/iafenvoy/mxt/util/math/Space.java` |
| `com/iafenvoy/saturn/util/MiscUtil` | `src/main/java/com/iafenvoy/mxt/util/MiscUtil.java` |
| `com/iafenvoy/saturn/util/OptionalBoolean` | `src/main/java/com/iafenvoy/mxt/util/OptionalBoolean.java` |
| `com/iafenvoy/saturn/util/OptionalFloat` | `src/main/java/com/iafenvoy/mxt/util/OptionalFloat.java` |
| `com/iafenvoy/saturn/util/PlayerNames` | `src/main/java/com/iafenvoy/mxt/util/PlayerNames.java` |
| `com/iafenvoy/saturn/util/SourceLedger` | `src/main/java/com/iafenvoy/mxt/util/SourceLedger.java` |
| `com/iafenvoy/saturn/util/TooltipText` | `src/main/java/com/iafenvoy/mxt/util/TooltipText.java` |
