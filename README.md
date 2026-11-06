# Volume Booster Free

Aplicativo Android independente, interface dark, sem anúncios, sem permissão de Internet e sem dependência da Google Play Store.

## Recursos iniciais
- Controle de ganho com `LoudnessEnhancer` (0–1000 mB no protótipo).
- Equalizador Android `Equalizer` por bandas disponíveis no aparelho.
- Interface escura e instalação por APK.
- Sem SDK de anúncios, analytics ou serviços Google.

## Limitações técnicas importantes
Os efeitos são solicitados no output mix (audio session 0). Muitos fabricantes/versões Android não permitem que aplicativos controlem globalmente o áudio de outros apps; nesses dispositivos o efeito pode não funcionar ou pode ser restrito ao próprio app. O Android não oferece uma API universal para amplificar todo áudio do sistema.
O slider não significa porcentagem física de volume. O ganho em millibels é limitado a 1000 mB neste protótipo para evitar prometer volume infinito; ganho excessivo causa clipping, distorção e risco auditivo. Testar em aparelhos reais é obrigatório.
O equalizador pode ser fornecido pelo fabricante e pode não estar disponível.

## Build no GitHub
1. Crie um repositório (ex.: `volume-booster-free`) no GitHub.
2. Envie todos os arquivos e pastas deste projeto para a raiz do repositório.
3. Abra **Actions** e selecione **Build Volume Booster Free APK**.
4. Clique em **Run workflow**.
5. Ao terminar, abra a execução e baixe o artefato `Volume-Booster-Free-debug`.
6. Extraia o APK e instale no Android permitindo instalação desta fonte.

O workflow também executa automaticamente a cada push na branch `main`.

## Assinatura
O artefato debug é assinado automaticamente com uma chave de debug efêmera do runner. Para distribuição e atualizações consistentes, configure uma chave própria em GitHub Secrets e adicione uma etapa `signingConfig` de release. Não compartilhe sua chave privada.

## Identidade
- Nome: Volume Booster Free
- applicationId: `com.volumebooster.free`
- Versão: 1.0.0
- Min Android: 6.0 (API 23)
- Sem permissão INTERNET.
