# portfolio

# first_team_project
프론트엔드: Javascript, HTML, CSS
  프레임워크: Vite
백엔드: Node.js
  프레임워크: Express
DB: MySQL

# 프로젝트 구동 
1. MySQL에 shoping_db 스키마 생성
   first_team_project에서 server폴더에 있는
   index.js에
   connection에 맞추어서 정보작성
   
2.백엔드
bash에서 server폴더에서 npm start

3.프론트엔드
bash에서 first_team_project폴더에서 npm install 후
npm run dev 

# 담당 작업
로그인 회원가입 기능
구매내역 
리뷰작성
리뷰게시판


# second_team_project
프론트엔드: Javascript, HTML, CSS
  프레임워크: React
백엔드: Java (Corretto Java 17)
  프레임워크: Spring
DB: MySQL
Data: Python(Crawling)

# 프로젝트 구동
1. MySQL에 2nd_pj 스키마 생성
   Intellij로 second_team_project에서
    src/main/resources폴더로 이동
   application.properties MySQL에 맞추어 정보 작성
    
2. 백엔드
   Intellij로 second_team_project폴더 열기(Amazon Corretto 17버전)
   build.gradle 열어서 dependencies 실행 후
   src/main/java/com/example/demo3 이동 후
   Demo3Application 열어서 실행

3. 프론트엔드
   bash에서 second_team_project/src/main/frontend에서 npm start로 실행
   
4. 관리자 아이디 만들기
   회원가입 진행
   완료후 MySQL로 이동 후
   2nd_pj 스키마로 이동 후
   update user set user_type = 2 where user_id = 1;
   SELECT * FROM 2nd_pj.user; 쿼리문 작성 후 실행
   관리자 계정으로 로그인 후 관리자 페이지로 이동
   데이터가 없습니다. 옆 등록하기 클릭
   크롤링한 데이터 book 테이블에 자동등록
      
p.s. Data Crawling
   second_team_project폴더에 data_crawling 폴더 이동 후 data_crawling.ipynb 파일 실행후 예스24 아이디 비밀번호 입력 후 
   셀 실행시 예스24 스테디셀러 부문 120권의 책 데이터 크롤링해서 csv파일로 저장
   이미지 파일은 book_images 폴더 생성 후 저장
   data_clean.ipynb 파일을 통해 MySQL형식에 맞게 데이터 정제
   해당 기능은 구현 후 
   정제된 csv파일은 second_team_project/src/main/data에 위치 시켰고
   이미지 파일 저장경로인 second_team_project/src/main/static/files에 이미지 파일들을 옮겼습니다
   해당 과정은 대략 15~20분이 소요됩니다
   오랜시간이 걸려 미리 세팅해두었습니다 
   따로 작동안하셔도 정상작동합니다

# 담당 작업

  1.책 보관서비스 신청
    - 해당 서비스 이용자가 제공한 ISBN 정보를 통해 중앙도서관 API를 이용하여 데이터 불러오기(중앙도서관 API에 데이터가 없을 시 수동 입력)
    - 다른 이용자에게 책 대여 여부 표시
    
  2. 관리자 페이지 책보관 관리 페이지 기능 구현
     - 책 보관 신청시 대여 여부에 따라 책을 보관
     - 전체보기 버튼 클릭시 보관상태로 분류하는 기능 구현
     - 페이지네이션 기능 구현
     - 북 테이블에 등록된적없는 ISBN이면 대여게시글쓰기 구역에 책정보 입력 버튼 활성화
       - 버튼으로 이동후 ISBN 옆 정보 가져오기 버튼을 통해 중앙도서관 API를 통해 제공된 세부 정보 데이터 불러오는 기능 구현
       - 책 등록후 상태에 보관승인(파랑) 버튼 클릭시 책보관중(붉은색)으로 변경
     - 유저가 반환 신청시 상태 반환승인(초록) 회사에서 책을 보관 확인후 반환 승인 클릭시 반환 완료(노랑) 상태로 변경
     - 검색기능   

  3. 마이페이지 나의 보관 내역
     - 보관중인 책 상세 페이지
       - 대여횟수, 최근 대여일 데이터 제공
     - 회사가 보관중인 책 반환신청 기능
     - 배열에 맞게 prev와 next 버튼으로 상세 페이지 이동 가능
     - 보관하는 책 검색기능
   
