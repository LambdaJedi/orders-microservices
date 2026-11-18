# Definition of Done

A story is done when all of these are true:

- [ ] Acceptance criteria in the backlog are met
- [ ] Unit or slice tests cover the new behaviour, including at least one failure case
- [ ] `mvn verify` passes locally and in CI
- [ ] No new compiler warnings or commented-out code
- [ ] Errors return a meaningful HTTP status and problem detail, not a stack trace
- [ ] README or API examples updated if behaviour changed
- [ ] Merged through a pull request that references the issue (for example "Closes #4")
