When working on this project, you must adhere to the following guidelines:

1. **Adhere to Existing Architectural Decision Records (ADRs):** Before making any changes, review
   the existing ADRs located in the `doc/adr` directory. Your work must be consistent with the
   decisions documented there, and you should not contradict or ignore any of the accepted ADRs.
   Superseded ADRs should be ignored, but you should still be aware of them for historical context.

2. **Create New ADRs When Appropriate:** For any significant architectural decision, you must 
   propose a new ADR, discuss it with user and then create the ADR doc. A decision is considered 
   "significant" if it affects the overall structure, dependencies, or technical approach of the 
   project. New ADRs are created with the help of the `adr` tool, which should be already installed.
   The command to create a new ADR is:
   ```bash
    adr new "Title of the ADR"
    ```
   A new ADR should be linked to the relevant existing ADRs, if applicable. The linking can be done
   using the `adr link` command, which allows you to specify the relationship type (e.g., 
   "amends"). The command to link an ADR is:
   ```bash
   adr link SOURCE LINK TARGET REVERSE-LINK
   ```
   where `SOURCE` is the number of the ADR you are linking from, `LINK` is the type of link (e.g., 
   "amends"), `TARGET` is the number of the ADR you are linking to, and `REVERSE-LINK` is the 
   type of link in the opposite direction (e.g., "is amended by").

3. **ADR Format:** Each new ADR must follow the standard format presented in 
   `doc/adr/templates/template.md`:
    * **Status:** One of following: "Accepted" or "Superseded"
    * **Brief Summary:** A concise description of the decision, typically in the form of "We are 
      going to do something because we want something else".
    * **Context:** Describe the problem or the need that requires a decision.
    * **Options:** Present and analyze at least two (preferably 4-5) potential solutions.
    * **Decision:** Clearly state the chosen solution. Justify why the chosen solution was 
      selected over the alternatives.
    * **Consequences:** Discuss the implications of the decision, including any risks or 
      challenges that may arise from it.
