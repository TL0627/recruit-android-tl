---

This application follows the principles outlined in the ​\*\*"Guide to App Architecture"\*\* by Google ([https://developer.android.com/topic/architecture](https://developer.android.com/topic/architecture)). Below, I will explain how the implementation adheres to these key principles.

### **Layered Architecture**

#### **1\. ​UI Layer**

The UI layer is responsible for presenting data to the user and collecting user input. It is implemented using Jetpack Compose, ensuring a modern and declarative UI approach.

* ​**Screens**:  
  Located in the package `nz.co.test.transactions.ui.screens`, this module contains three composables that represent the app's screens. These composables are pure UI components, decoupled from business logic, and are independently testable. Test cases for each screen are included to ensure correctness.  
* ​**ViewModels**:  
  Located in the package `nz.co.test.transactions.ui.viewmodels`, this module contains two ViewModels implemented using Jetpack's `ViewModel` library. These ViewModels are injected at runtime using Hilt for dependency injection. They handle UI-related logic and act as intermediaries between the UI and the domain layer. Each ViewModel is independently testable, with test cases included for verification.

#### **2\. ​Domain Layer**

The domain layer contains the business logic of the application, ensuring separation of concerns and maintainability.

* ​**Interactor**:  
  Located in the package `nz.co.test.transactions.domain`, this module contains a single Interactor that encapsulates the core business logic. It is designed to be independent of both the UI and data layers, making it easily testable. Test cases are included to validate the correctness of the business logic.

#### **3\. ​Data Layer**

The data layer is responsible for managing data sources and persistence. It is implemented as a separate module to allow reuse across other app modules if needed.

* ​**Repository**:  
  The repository, located in the data layer, acts as an abstraction over the data sources. It hides the details of local and remote data sources and exposes a clean interface to the domain layer. This ensures that the domain layer does not need to worry about the underlying data implementation. The repository is independently testable, with test cases included.  
* ​**Data Sources**:  
  The data layer includes two distinct data sources:  
  * ​**RemoteDataSource**:  
    This interface communicates with the remote backend via a RESTful API. It is designed to handle network-related operations and is independently testable, with test cases included.  
  * ​**LocalDataSource**:  
    This interface interacts with the local database for persistent data storage. It abstracts away the database implementation details, ensuring clean separation of concerns. It is also independently testable, with test cases included.

---

### 

### **Key Benefits of This Architecture**

1. ​**Separation of Concerns**  
   Each layer has a well-defined responsibility, ensuring a clean and modular codebase. This separation makes the application easier to understand, maintain, and extend.  
2. ​**Drive UI from Data Models**  
   The UI is powered by UI states, which are emitted via `Flow` objects. These `Flow` objects observe updates from the lower-level database, ensuring that the UI always reflects the latest data in a reactive and declarative manner.  
3. ​**Single Source of Truth**  
   The database serves as the single source of truth for the application. All UI updates are derived directly from the data stored in the database, eliminating inconsistencies and ensuring data integrity.  
4. ​**Unidirectional Data Flow**  
   The architecture enforces a clear unidirectional data flow. User interactions (e.g., calculating GST) trigger updates in the database, and these updates are then propagated to the UI. This approach simplifies reasoning about the application's behavior and reduces the likelihood of bugs caused by complex, bidirectional interactions.

By adhering to these principles, the application achieves a clean, scalable, and maintainable architecture that ensures a high-quality user experience.

---

### Changes from the Original Codebase

1. ​Introduction of a Dependency Catalog  
   A dependency catalog has been introduced to streamline and centralize the management of dependencies across the project. This improves maintainability and ensures consistency in dependency versions throughout the codebase.  
2. ​Removal of View/XML UI SDK  
   All codes and resources related to the traditional View/XML-based UI SDK have been removed. The application now fully adopts Jetpack Compose for building the user interface, leveraging its modern, declarative approach for a more efficient and scalable UI development process.  
3. ​Replacement of Dagger with Hilt  
   The Dagger dependency injection framework has been replaced with Hilt. Hilt provides a more streamlined and type-safe way to manage dependency injection, reducing boilerplate code and improving developer productivity. This change aligns with the modern best practices for dependency management in Android development.  

### Extra Features Implemented

1. ​GST Calculation  
   * The application calculates the amount of GST paid on each transaction.  
   * GST is applied at a standard rate of 15% and is displayed alongside transaction details for clarity.  
   * The GST information is presented in a user-friendly manner, ensuring that users can easily understand the tax breakdown for each transaction.  
2. ​Color-Coded Transaction Amounts  
   * Transaction amounts are color-coded to provide a quick visual cue about the type of transaction:  
     * ​Credit amounts are displayed in ​green, indicating inflows.  
     * ​Debit amounts are displayed in ​red, indicating outflows.  
   * This enhancement improves usability by making it easier for users to distinguish between different types of transactions at a glance.  
3. ​Narration/Talkback Support  
   * The app includes support for screen readers and talkback functionality, ensuring accessibility for users with visual impairments.  
   * Descriptive narrations are provided for key UI elements, enabling users to navigate and interact with the app effectively using assistive technologies.

---

