# Public Service Basic Template
## 프로젝트 모듈명 변경 방법
### 1. root 프로젝트 모듈명 변경
1. root project 우클릭
2. Refactor -> Rename.. 
3. Enter New Module Name : 프로젝트 모듈명 지정.
   - Naming rule: nuri-[프로젝트명]-[마이크로서비스명]
   - ex. nuri-aimir-auth

### 2. sub project 모듈명 변경
1. 각 sub project 우클릭
2. Refactor -> Rename...
3. Rename Module and Directory 선택
4. 서브 프로젝트 모듈명 지정 : 
    - Naming rule: nuri-[프로젝트명]-[서비스명]-[모듈명]
    - ex. nuri-aimir-auth-boot, nuri-aimir-auth-service

### 3. 각 프로젝트 패키지명 변경 
1. 패키지(com.nuri.user) 선택 후 우클릭 
2. Refactor -> Rename..
3. Rename Directory(디렉토리명 변경)
4. user -> [프로젝트명]으로 변경
    - com.nuri 까지는 공통, 기존 user -> [프로젝트명]으로 변경.
    - ex. com.nuri.aimir : 프로젝트의 공통 패키지명으로 사용
5. 각 프로젝트마다 동일 작업 

### 4. root project (main) pom.xml 수정
1. project.main.name 프로퍼티 수정 : nuri-[프로젝트명]-[서비스명]
2. project.main.package 프로퍼티 수정 : com.nuri.[프로젝트명]
3. oneself module setting 수정
   - groupId : com.nuri.[프로젝트명]
   - artifactId : nuri-[프로젝트명]-[서비스명]

### 5. sub project (module) pom.xml 수정
1. parent module setting 수정
   - groupId : com.nuri.[프로젝트명]
   - artifactId : nuri-[프로젝트명]-[서비스명]



### 6. Maven Dependency Update
1. root 프로젝트에서 mvn clean
2. root 프로젝트에서 mvn package
3. 에러가 없는지 확인. 
