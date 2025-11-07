# Docker �궗�슜 媛��씠�뱶

## ? Docker���?

Docker�뒗 �븷�뵆由ъ���씠�뀡�쓣 而⑦뀒�씠�꼫濡� �뙣�궎吏뺥븯�뿬 �뼱�뵒�꽌�굹 �룞�씪�븯寃� �떎�뻾�븷 �닔 �엳寃� �빐二쇰뒗 �룄援ъ엯�땲�떎.

## ? 鍮좊Ⅸ �떆�옉

### 1. �궗�쟾 以�鍮�

```bash
# Docker �꽕移� �솗�씤
docker --version
docker-compose --version
```

### 2. �븷�뵆由ъ���씠�뀡 鍮뚮뱶

```bash
# Maven�쑝濡� JAR �뙆�씪 鍮뚮뱶
mvn clean package
```

### 3. Docker �씠誘몄�� 鍮뚮뱶 諛� �떎�뻾

```bash
# docker-compose濡� �븳 踰덉뿉 鍮뚮뱶 諛� �떎�뻾
docker-compose up -d

# �삉�뒗 �떒怨꾨퀎濡� �떎�뻾
# 1. �씠誘몄�� 鍮뚮뱶
docker-compose build

# 2. 而⑦뀒�씠�꼫 �떎�뻾
docker-compose up -d
```

### 4. �젒�냽 �솗�씤

- �븷�뵆由ъ���씠�뀡: http://localhost:8080
- API �뀒�뒪�듃: http://localhost:8080/api/lotto/draw
- �뜲�씠�꽣踰좎씠�뒪: localhost:3306

## ? Docker 紐낅졊�뼱

### 湲곕낯 紐낅졊�뼱

```bash
# 而⑦뀒�씠�꼫 �떎�뻾 (諛깃렇�씪�슫�뱶)
docker-compose up -d

# 而⑦뀒�씠�꼫 以묒��
docker-compose down

# 而⑦뀒�씠�꼫 以묒�� 諛� 蹂쇰ⅷ �궘�젣 (?? �뜲�씠�꽣 �궘�젣�맖)
docker-compose down -v

# 濡쒓렇 �솗�씤
docker-compose logs -f

# �긽�깭 �솗�씤
docker-compose ps

# �옱鍮뚮뱶
docker-compose up --build
```

### 媛쒕퀎 �꽌鍮꾩뒪 �젣�뼱

```bash
# �븷�뵆由ъ���씠�뀡留� �옱�떆�옉
docker-compose restart app

# �뜲�씠�꽣踰좎씠�뒪留� �옱�떆�옉
docker-compose restart db

# 濡쒓렇 �솗�씤 (�듅�젙 �꽌鍮꾩뒪)
docker-compose logs -f app
docker-compose logs -f db
```

## ? �꽕�젙 蹂�寃�

### �솚寃� 蹂��닔 蹂�寃�

`docker-compose.yml` �뙆�씪�쓽 `environment` �꽮�뀡�쓣 �닔�젙:

```yaml
environment:
  - SPRING_DATASOURCE_PASSWORD=your_password
```

蹂�寃� �썑 �옱�떆�옉:

```bash
docker-compose down
docker-compose up -d
```

### �룷�듃 蹂�寃�

`docker-compose.yml` �뙆�씪�쓽 `ports` �꽮�뀡�쓣 �닔�젙:

```yaml
ports:
  - "8081:8080"  # �샇�뒪�듃 �룷�듃瑜� 8081濡� 蹂�寃�
```

## ?? �뜲�씠�꽣踰좎씠�뒪 愿�由�

### �뜲�씠�꽣踰좎씠�뒪 �젒�냽

```bash
# 而⑦뀒�씠�꼫 �궡遺��뿉�꽌 �젒�냽
docker-compose exec db mysql -u root -p1234 lotto

# �삉�뒗 �쇅遺��뿉�꽌 �젒�냽
mysql -h localhost -P 3306 -u root -p1234 lotto
```

### �뜲�씠�꽣 諛깆뾽

```bash
# �뜲�씠�꽣踰좎씠�뒪 �뜡�봽
docker-compose exec db mysqldump -u root -p1234 lotto > backup.sql
```

### �뜲�씠�꽣 蹂듭썝

```bash
# �뜲�씠�꽣踰좎씠�뒪 蹂듭썝
docker-compose exec -T db mysql -u root -p1234 lotto < backup.sql
```

## ? 臾몄젣 �빐寃�

### 而⑦뀒�씠�꼫媛� �떆�옉�릺吏� �븡�뒗 寃쎌슦

```bash
# 濡쒓렇 �솗�씤
docker-compose logs

# 而⑦뀒�씠�꼫 �긽�깭 �솗�씤
docker-compose ps

# �옱鍮뚮뱶
docker-compose up --build
```

### �룷�듃媛� �씠誘� �궗�슜 以묒씤 寃쎌슦

```bash
# �룷�듃 �궗�슜 �솗�씤
netstat -ano | findstr :8080
netstat -ano | findstr :3306

# docker-compose.yml�뿉�꽌 �룷�듃 蹂�寃�
```

### �뜲�씠�꽣踰좎씠�뒪 �뿰寃� �삤瑜�

1. �뜲�씠�꽣踰좎씠�뒪 而⑦뀒�씠�꼫媛� �떎�뻾 以묒씤吏� �솗�씤
2. �꽕�듃�썙�겕 �꽕�젙 �솗�씤
3. �솚寃� 蹂��닔 �솗�씤

## ? 異붽�� �븰�뒿 �옄猷�

- [Docker 怨듭떇 臾몄꽌](https://docs.docker.com/)
- [Docker Compose 怨듭떇 臾몄꽌](https://docs.docker.com/compose/)

