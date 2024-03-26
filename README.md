## Harness2Flagd

## Words
- [Harness.io](https://harness.io/) is a Software Delivery Saas which includes a [Feature Flags](https://www.harness.io/products/feature-flags) feature.
- [OpenFeature](https://openfeature.dev/) is a [CNCF](https://www.cncf.io/) project to standardize Feature Flag systems.
- [Flagd](https://flagd.dev/) is an OpenFeature provider supporting in-process evaluation and local files. [(at least the Java SDK does)](https://github.com/open-feature/java-sdk-contrib/tree/main/providers/flagd)

## This repo?

I am used to Gitops for remote software configuration, so I was looking for a Feature Flag provider which centered its offering on Git. *As an aside, I do trust Github or a CDN uptime, but I have a hard time trusting a smaller Saas vendors, whether it's polling or [streaming changes](https://www.getunleash.io/blog/streaming-flags-is-a-paper-tiger). Plus, some FF vendors have [very creative pricing](https://prefab.cloud/pricing/) models.*

[Featurevisor](https://featurevisor.com/docs/concepts/gitops/) is a vendor that shares my vision, but it doesn't have UX where you can edit flags.

Harness offers a [Git sync](https://www.harness.io/blog/feature-flags-gitops) feature for many Git hosts aswell as a decent web UX.

#### This project takes all of the Harness .yaml files in the working directory and converts them to Flagd json files to a /generated folder.

For each input file, there will be 1 output per Harness environment.

## Usage with Github Actions

Let's say you have a repo synced with Harness, you can add a Github Action like this one:
```yaml
name: CI

on: push

jobs:
  harness-to-flagd:
    runs-on: ubuntu-latest

    permissions:
      contents: write

    steps:
      - uses: actions/checkout@v4

      - uses: robinraju/release-downloader@v1.9
        with:
          repository: "Minikloon/Harness2Flagd"
          tag: "v0.2-dev"
          fileName: "harness2flagd-1.0-SNAPSHOT.jar"
      
      - uses: actions/setup-java@v4
        with:
          distribution: 'corretto'
          java-version: '21'

      - run: java -jar harness2flagd-1.0-SNAPSHOT.jar

      - uses: stefanzweifel/git-auto-commit-action@v5
        with:
          commit_message: Auto-Converted Harness YAML to Flagd
          file_pattern: 'generated/*'
```

## Notes
Targetting options are not supported at this time, one reason is because the targets/groups associations aren't synced in git by Harness, other reason is I don't need it myself.

If you use Harness, consider getting one of their paid plans.
