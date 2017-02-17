/**
 * This package contains a set of utilities for working with
 * lambdas that may throw (un)checked exceptions. This includes:
 * <ul>
 *     <li>
 *         {@link com.github.kahalemakai.safely.Safely Safely} –
 *         a utility for wrapping lambdas
 *     </li>
 *     <li>
 *         {@link com.github.kahalemakai.safely.SafeCallable SafeCallable} –
 *         an interface similarly to {@link java.util.concurrent.Callable Callable}
 *         that does not throw checked exceptions
 *     </li>
 *     <li>
 *         {@link com.github.kahalemakai.safely.WrappedException WrappedException} –
 *         an exception that easily wraps around (probably checked) exceptions
 *     </li>
 * </ul>
 */
package com.github.kahalemakai.safely;
