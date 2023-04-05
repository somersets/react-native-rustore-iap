# React Native RuStore IAP
Данная библиотека является оберткой над RuStore SDK

## Установка

```sh
npm install @devsomersets/react-native-rustore-iap
```

```
android/build.gradle

allprojects {
    repositories {
        ...
        maven {
            url("https://artifactory-external.vkpartner.ru/artifactory/maven")
        }
    }
}
```
---
```
android/app/build.gradle

dependencies {
    ...
    implementation("ru.rustore.sdk:billingclient:0.1.7")
}
```
При разработке локально дебаг билд нужно подписывать тем кейстором, который использовался при загрузке приложения в RuStore Console.

Для корректной установки pod'ов нужно установить зависимость в modular_headers => false
```
pod 'react-native-rustore-iap', :podspec => '../node_modules/react-native-rustore-iap/react-native-rustore-iap.podspec', :modular_headers => false
```

## Expo plugin
Поддержка Managed проектов Expo c expo-config-plugin

```
app.json
...
"plugins": [
      ["@devsomersets/react-native-rustore-iap"],
      ["expo-build-properties",
      {
        "android": {
          "minSdkVersion": 23
        }
      }
    ]],
...
```

---

## Проверка доступности работы с платежами
Для проверки доступности платежей вызовите метод checkRuStorePurchasesAvailability.

При его вызове проверяются следующие условия:

1. На устройстве пользователя должен быть установлен RuStore.

2. RuStore должен поддерживать функциональность платежей.

3. Пользователь должен быть авторизован в RuStore.

4. Пользователь и приложение не должны быть заблокированы в RuStore.

5. Для приложения должна быть включена возможность покупок в системе RuStore Консоль.

## Использование

```tsx
import { initializeRuStore, checkRuStoreAvailable } from '@devsomersets/react-native-rustore-iap';
import { useEffect } from 'react';
import { View } from 'react-native';

const YOUR_APP_ID: string = "YOUR_RUSTORE_CONSOLE_APP_ID";

function App() {
  useEffect(() => {
    initializeRuStore(YOUR_APP_ID);
    checkRuStoreAvailable().then((isAvailable) => {
      if (typeof isAvailable === "string") {
        // handle error
        // (https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Error-processing)
      } else {
        // isAvailable will be true
      }
    })

  }, []);

  return <View />;
}

```

## Инициализация модуля
```ts
import { initializeRuStore } from '@devsomersets/react-native-rustore-iap';

initializeRuStore(YOUR_CONSOLE_APP_ID);
```

## Проверка доступности работы
```ts
import { checkRuStoreAvailable } from '@devsomersets/react-native-rustore-iap';

const isRuStoreAvailable: Boolean | String = await checkRuStoreAvailable();
```
Обработка ошибок (https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Error-processing)

## Получение актуальной информации по списку продуктов

Для получения списка продуктов используйте метод getProducts:

```ts
import { getRuStoreProducts } from '@devsomersets/react-native-rustore-iap';

const products: RuStoreProduct[] = await getRuStoreProducts(productsIds: String[]);
```


## Получение списка покупок пользователя

```ts
import { getRuStorePurchases } from '@devsomersets/react-native-rustore-iap';

const products: RuStorePurchase[] = await getRuStorePurchases();
```

## Покупка продукта

```ts
import { purchaseRuStoreProduct } from '@devsomersets/react-native-rustore-iap';

const purchaseResult: PaymentResult | InvalidPurchaseResult = await purchaseRuStoreProduct(product: RuStoreProduct, developerPayload?: string);
```

## Потребление покупки

```ts
import { confirmRuStorePurchase } from '@devsomersets/react-native-rustore-iap';

const confirmResponse: ConfirmPurchaseResponse = await confirmRuStorePurchase(purchaseId: string, developerPayload?: string);
```

## Отмена покупки

```ts
import { deleteRuStorePurchase } from '@devsomersets/react-native-rustore-iap';

const deletePurchaseResponse: DeletePurchaseResponse = await deleteRuStorePurchase(purchaseId: string);
```

## Сценарий потребления и отмены покупки

https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/%20consumption-and-withdrawal

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
