import{e}from"./app.adf81b97.js";import{_ as a}from"./plugin-vue_export-helper.21dcd24c.js";const t={},i=e('<h1 id="how-to-review-code" tabindex="-1"><a class="header-anchor" href="#how-to-review-code" aria-hidden="true">#</a> How to review code</h1><h2 id="common" tabindex="-1"><a class="header-anchor" href="#common" aria-hidden="true">#</a> Common</h2><ul><li>Variables in package order (batch, domain, dto, repository, service...), then strategy.</li></ul><h2 id="core-code-not-tests-not-archetypes" tabindex="-1"><a class="header-anchor" href="#core-code-not-tests-not-archetypes" aria-hidden="true">#</a> Core code (not tests &amp; not archetypes)</h2><ul><li>Check class comment and see if there is a right usage of {@link}.</li><li>Instance variable should have the same name as its class like: <code>UserService userService</code>.</li><li>No new line between method start ({) and method end (}).</li><li>When a variable represents an id, say it in the name like &#39;carId&#39;.</li><li>When a variable is DTO, say it in the name like <code>carDTO</code>.</li><li>Check logs texts.</li><li>No point at the end of logs texts.</li><li>Add @ExcludeFromCoverageGeneratedReport when it&#39;s required.</li><li>Use of Optional.ofNullable() instead of &quot;if (x != null) return x.getAnyValue()&quot;.</li><li>In services, first line of implementations is a log.</li></ul><h2 id="test-code" tabindex="-1"><a class="header-anchor" href="#test-code" aria-hidden="true">#</a> Test code</h2><ul><li>No comment on class.</li><li>Test class with Package - Class <code>@DisplayName(&quot;Batch - Account flux&quot;)</code>.</li><li>Method name and display name should declare with it tests <code>@DisplayName(&quot;Check received data&quot;)</code></li></ul>',7);function o(s,r){return i}var c=a(t,[["render",o]]);export{c as default};