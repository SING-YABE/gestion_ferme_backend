# Guide de déploiement sur Render

## 1. Pousser le code sur GitHub

```bash
git add .
git commit -m "feat: add Dockerfile and Render config for production"
git push origin main
```

## 2. Créer le service sur Render

1. Aller sur https://dashboard.render.com
2. Cliquer **New → Blueprint** (utilise render.yaml automatiquement)
3. Connecter le repo GitHub `gestion-parc-backend`
4. Render détecte `render.yaml` et crée automatiquement :
   - Le Web Service Kotlin
   - La base PostgreSQL

> Alternative manuelle : New → Web Service → Docker → connecter le repo

## 3. Migrer la base de données locale

### Exporter depuis ta machine locale
```bash
pg_dump -U postgres -d snackboost_db -f backup.sql
```

### Importer sur Render
Récupère la **External Database URL** depuis Render Dashboard → PostgreSQL → Info

```bash
psql "postgresql://USER:PASSWORD@HOST:PORT/snackboost_db" < backup.sql
```

> L'URL complète est disponible dans Render sous "External Database URL"

## 4. Variables d'environnement sur Render

Render injecte `DATABASE_URL` automatiquement via render.yaml.
Vérifier dans : Dashboard → gestion-parc-backend → Environment

| Variable      | Valeur                        |
|---------------|-------------------------------|
| DATABASE_URL  | (auto depuis la DB Render)    |
| UPLOAD_DIR    | /app/uploads                  |

## 5. Tester l'API déployée

Une fois déployé, l'URL sera du type :
```
https://gestion-parc-backend.onrender.com
```

Tester :
```
GET https://gestion-parc-backend.onrender.com/api/animaux
```

> ⚠️ Le 1er démarrage peut prendre 2-3 minutes (build Docker)
> ⚠️ Sur le plan gratuit, le service s'endort après 15 min d'inactivité

## 6. Mettre à jour Flutter

Dans `lib/core/constants/app_constants.dart` :
```dart
static const String baseUrl = 'https://gestion-parc-backend.onrender.com';
```

## 7. Générer l'APK

```bash
cd farm_management_app
flutter build apk --release
```

APK disponible dans :
```
build/app/outputs/flutter-apk/app-release.apk
```
