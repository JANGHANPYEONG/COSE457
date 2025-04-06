# Vector Graphics Editor

벡터 그래픽 에디터 프로젝트입니다. 다양한 디자인 패턴을 적용하여 확장 가능하고 유지보수하기 쉬운 구조로 설계되었습니다.

## 프로젝트 구조

### Core Components

- `Shape`: 모든 도형의 기본이 되는 추상 클래스
  - 위치(x, y), 크기(width, height)
  - 색상(fillColor, strokeColor)
  - 테두리 두께(strokeWidth)
  - 선택 상태(selected)
  - z-order 관리
  - Observer 패턴 적용

### Shapes

- `RectangleShape`: 사각형 도형
- `EllipseShape`: 타원 도형
- `LineShape`: 선 도형
- `TextShape`: 텍스트 도형
- `ImageShape`: 이미지 도형
- `FreeDrawPath`: 자유 곡선 도형
- `GroupShape`: 그룹 도형 (Composite 패턴)

### Tools

- `Tool`: 모든 도구의 기본이 되는 인터페이스
  - 마우스 이벤트 처리
  - 상태 관리 (State 패턴)

### Commands

- `Command`: 명령 패턴 인터페이스
  - 실행(execute)
  - 취소(undo)
- `CommandManager`: 명령 관리자
  - 명령 스택 관리
  - 실행/취소/재실행 기능

## 적용된/되어야 할할 디자인 패턴

1. **Observer 패턴**

   - Shape의 속성 변경을 UI에 즉시 반영
   - ShapeObserver 인터페이스를 통한 변경 사항 전파

2. **Command 패턴**

   - 작업의 실행/취소/재실행 관리
   - CommandManager를 통한 명령 스택 관리

3. **State 패턴**

   - Tool의 상태 관리
   - ToolState 인터페이스를 통한 상태 전환

4. **Composite 패턴**

   - GroupShape를 통한 도형 그룹화
   - 개별 도형과 그룹을 동일하게 처리

5. **Abstract Factory 패턴**
   - 도형 생성 팩토리
   - 도구 생성 팩토리

## 주요 기능

1. 도형 생성 및 편집

   - 다양한 도형 타입 지원
   - 위치 및 크기 조절
   - 색상 및 스타일 변경

2. 다중 선택

   - 여러 도형 동시 선택
   - 그룹화 기능

3. 속성 관리

   - 선택된 도형의 속성 표시
   - 실시간 속성 변경 반영

4. z-order 관리
   - 도형의 순서 조절
   - 앞/뒤로 이동

## 확장 기능

1. 텍스트 표시

   - 도형 내부 텍스트
   - 폰트 및 스타일 설정

2. 프레임 표시

   - 도형 외부 액자
   - 다양한 프레임 스타일

3. 그림자 효과
   - 도형에 그림자 추가
   - 그림자 스타일 설정

## 개발 환경

- Java

## 참고사항

- 캔버스 그래픽 라이브러리 사용 가능
- 외부 라이브러리 사용 제한 없음
