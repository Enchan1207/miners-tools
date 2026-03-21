# Development guide

本MODはFabricのMODサンプルをベースに構築しています。

## Development

### Build

以下のコマンドでMODをビルドします:

```sh
./gradlew build
```

成功すると、ファイル `/build/libs/miners-tools-x.x.x.jar` が生成されます。  
(VSCode workspace を利用している場合、エクスプローラーの `dist` ルートに表示されます)

### Generate sources

Fabricの機能でMinecraftのソースをdecompileすることができます:

```sh
./gradlew genSources
```

これにより、 `net.minecraft` パッケージのソースをIDEから参照できるようになります。

### Run client

以下のコマンドで、MOD適用状態のMinecraftが起動します:

```sh
./gradlew runClient
```

## References

実装にあたり、以下のドキュメントを参照しています:

- [FabricMC/fabric-example-mod](https://github.com/FabricMC/fabric-example-mod/tree/1.20)
- [Setting up a mod development environment [Fabric Wiki]](https://fabricmc.net/wiki/tutorial:setup)
- [Developer Guides | Fabric Documentation](https://docs.fabricmc.net/1.20.4/develop/)
