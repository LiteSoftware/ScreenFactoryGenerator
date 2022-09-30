# ScreenFactoryGenerator

This lib is needed in order to generate functions for creating **fragments** and **activities**.


```kotlin
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.ksp.*

object MainFragmentScreen : BaseScreen {

    fun newInstance(test1: Int, test2: String) = MainFragment().apply {
       arguments = bundleOf("TEST1" to test1,"TEST2" to test2)
    }

    fun MainFragment.bind() {
       test1 = arguments?.get("TEST1") as Int
       test2 = arguments?.get("TEST2") as String
    }
}
```

```kotlin
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.ksp.*

object MainActivityScreen : BaseScreen {

    fun newIntent(context: Context, test1: String): Intent { 
       val intent = Intent(context, MainActivity::class.java)
       intent.putExtra("TEST1", test1)
       return intent
    }

    fun MainActivity.bind() {
       test1 = intent.getStringExtra("TEST1")!!
    }
}
```

[![](https://jitpack.io/v/LiteSoftware/ScreenFactoryGenerator.svg)](https://jitpack.io/#LiteSoftware/ScreenFactoryGenerator)

## How use

1. Add ksp plugin:

```groovy
repositories {
    gradlePluginPortal()
    // ...
}
```

```groovy
plugins {
    // ...
    id 'com.google.devtools.ksp' version '1.7.10-1.0.6'
}
```

2. Specify where the generated files will be located

```groovy
android {
//..
kotlin {
        sourceSets.debug {
            kotlin.srcDirs += 'build/generated/ksp/debug/kotlin'
        }
        sourceSets.release {
            kotlin.srcDirs += 'build/generated/ksp/release/kotlin'
        }
    }
}
```

3. Download using Gradle

Add this in your root `build.gradle` at the end of `repositories` in `allprojects` section:
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Then add this dependency to your **module-level** `build.gradle` in `dependencies` section:
```groovy
implementation 'com.github.LiteSoftware.ScreenFactoryGenerator:annotation:$version'
ksp 'com.github.LiteSoftware.ScreenFactoryGenerator:processor:$version'

```

4. Annotate activities or fragments with **@JScreen** annotation

```kotlin
@JScreen(generateScreenMethod = false)
class MainActivity : AppCompatActivity() {
// ...
```

5. Add properties as arguments. To do this, you need to mark the parameters with the annotation **@JParam**

```kotlin
// ...
    @JParam
    var test1: String = ""
// ...
```

6. Compile the application

7. That's all) you can Use the generated classes and methods

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ...
        bind() // must be called in order to fill in your arguments
    }
```

```kotlin
val intent = MainActivityScreen.newIntent(this, "Hello World!!!")
startActivity(intent)
```


---

### License

```
   Copyright 2022 Vitaliy Sychov. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
