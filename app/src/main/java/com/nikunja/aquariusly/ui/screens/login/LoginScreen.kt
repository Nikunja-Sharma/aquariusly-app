package com.nikunja.aquariusly.ui.screens.login

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.nikunja.aquariusly.R
import com.nikunja.aquariusly.ui.components.AquariuslyLogo
import com.nikunja.aquariusly.ui.theme.UnifiedTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LoginContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onGoogleSignInClick = {
            viewModel.setSigningIn(true)
            scope.launch {
                signInWithGoogle(
                    context = context,
                    onTokenReceived = { idToken ->
                        viewModel.signInWithGoogle(idToken)
                    },
                    onError = { error ->
                        viewModel.setSigningIn(false)
                        scope.launch {
                            snackbarHostState.showSnackbar(error)
                        }
                    }
                )
            }
        },
        onEmailSignInClick = {
            viewModel.setSigningIn(true)
            scope.launch {
                signInWithEmail(
                    email = state.email,
                    password = state.password,
                    isSignUp = state.isSignUp,
                    onTokenReceived = { idToken ->
                        viewModel.signInWithFirebaseEmail(idToken)
                    },
                    onError = { error ->
                        viewModel.setSigningIn(false)
                        scope.launch {
                            snackbarHostState.showSnackbar(error)
                        }
                    }
                )
            }
        },
        onToggleEmailForm = viewModel::toggleEmailForm,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onToggleSignUp = viewModel::toggleSignUpMode
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit,
    onToggleEmailForm: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleSignUp: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Scaffold(
        containerColor = colors.backgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            AquariuslyLogo(size = 100.dp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Aquariusly",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your AI companion",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AIModelBadges()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(visible = state.showEmailForm) {
                EmailLoginForm(
                    email = state.email,
                    password = state.password,
                    isSignUp = state.isSignUp,
                    isLoading = state.isLoading || state.isSigningIn,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onSubmit = onEmailSignInClick,
                    onToggleSignUp = onToggleSignUp
                )
            }
            
            if (!state.showEmailForm) {
                GoogleSignInButton(
                    onClick = onGoogleSignInClick,
                    isLoading = state.isLoading || state.isSigningIn
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                EmailSignInButton(
                    onClick = onToggleEmailForm,
                    isLoading = state.isLoading || state.isSigningIn
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(onClick = onToggleEmailForm) {
                    Text(
                        text = "Back to other options",
                        color = colors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun EmailLoginForm(
    email: String,
    password: String,
    isSignUp: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleSignUp: () -> Unit
) {
    val colors = UnifiedTheme.colors
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accentBlue,
                unfocusedBorderColor = colors.borderDefault
            )
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                TextButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = if (passwordVisible) "Hide" else "Show",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.accentBlue
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.length >= 6) onSubmit()
                }
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accentBlue,
                unfocusedBorderColor = colors.borderDefault
            )
        )
        
        Button(
            onClick = onSubmit,
            enabled = !isLoading && email.isNotBlank() && password.length >= 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.accentBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colors.textPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isSignUp) "Create Account" else "Sign In",
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted
            )
            Text(
                text = if (isSignUp) "Sign In" else "Sign Up",
                style = MaterialTheme.typography.bodySmall,
                color = colors.accentBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(enabled = !isLoading) { onToggleSignUp() }
            )
        }
    }
}

@Composable
private fun AIModelBadges() {
    val colors = UnifiedTheme.colors
    
    val models = listOf(
        "ChatGPT" to colors.accentMint,
        "Claude" to colors.accentAmber,
        "Gemini" to colors.accentBlue,
        "Grok" to colors.error
    )
    
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        models.forEach { (name, color) ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = color.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val colors = UnifiedTheme.colors
    
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.surfaceElevated,
            contentColor = colors.textPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = colors.accentBlue,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmailSignInButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val colors = UnifiedTheme.colors
    
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                modifier = Modifier.size(20.dp),
                tint = colors.textPrimary
            )
            
            Text(
                text = "Continue with Email",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
        }
    }
}

private suspend fun signInWithGoogle(
    context: Context,
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)
        
        val webClientId = context.getString(R.string.default_web_client_id)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(webClientId)
            .build()
        
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken
        
        onTokenReceived(idToken)
    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
        onError("No Google account found. Please add a Google account to your device.")
    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
        onError("Sign in cancelled")
    } catch (e: Exception) {
        onError("Sign in failed: ${e.message}")
    }
}

private suspend fun signInWithEmail(
    email: String,
    password: String,
    isSignUp: Boolean,
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val auth = FirebaseAuth.getInstance()
        
        val result = if (isSignUp) {
            auth.createUserWithEmailAndPassword(email, password).await()
        } else {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        
        val idToken = result.user?.getIdToken(false)?.await()?.token
        
        if (idToken != null) {
            onTokenReceived(idToken)
        } else {
            onError("Failed to get authentication token")
        }
    } catch (e: Exception) {
        val message = when {
            e.message?.contains("email address is badly formatted") == true -> "Invalid email format"
            e.message?.contains("password is invalid") == true -> "Incorrect password"
            e.message?.contains("no user record") == true -> "No account found with this email"
            e.message?.contains("email address is already in use") == true -> "Email already registered. Try signing in."
            e.message?.contains("password should be at least 6 characters") == true -> "Password must be at least 6 characters"
            else -> e.message ?: "Authentication failed"
        }
        onError(message)
    }
}
