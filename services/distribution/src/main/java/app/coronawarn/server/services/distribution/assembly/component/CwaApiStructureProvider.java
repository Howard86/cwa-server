

package app.coronawarn.server.services.distribution.assembly.component;

import app.coronawarn.server.services.distribution.assembly.structure.WritableOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.directory.Directory;
import app.coronawarn.server.services.distribution.assembly.structure.directory.IndexDirectoryOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.directory.decorator.indexing.IndexingDecoratorOnDisk;
import app.coronawarn.server.services.distribution.config.DistributionServiceConfig;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Assembles the content underneath the {@code /version} path of the CWA API.
 */
@Component
public class CwaApiStructureProvider {

  private final AppConfigurationStructureProvider appConfigurationStructureProvider;
  private final StatisticsStructureProvider statisticsStructureProvider;
  private final DiagnosisKeysStructureProvider diagnosisKeysStructureProvider;
  private final DistributionServiceConfig distributionServiceConfig;

  /**
   * Creates a new CwaApiStructureProvider.
   */
  CwaApiStructureProvider(
      AppConfigurationStructureProvider appConfigurationStructureProvider,
      StatisticsStructureProvider statisticsStructureProvider,
      DiagnosisKeysStructureProvider diagnosisKeysStructureProvider,
      DistributionServiceConfig distributionServiceConfig) {
    this.appConfigurationStructureProvider = appConfigurationStructureProvider;
    this.statisticsStructureProvider = statisticsStructureProvider;
    this.diagnosisKeysStructureProvider = diagnosisKeysStructureProvider;
    this.distributionServiceConfig = distributionServiceConfig;
  }

  /**
   * Returns the base directory.
   */
  public Directory<WritableOnDisk> getDirectory() {
    IndexDirectoryOnDisk<String> versionDirectory = new IndexDirectoryOnDisk<>(
        distributionServiceConfig.getApi().getVersionPath(),
        ignoredValue -> Set.of(distributionServiceConfig.getApi().getVersionV1()),
        Object::toString);

    versionDirectory.addWritableToAll(
        ignoredValue -> Optional.of(appConfigurationStructureProvider.getAppConfiguration()));
    versionDirectory.addWritableToAll(
        ignoredValue -> Optional.ofNullable(appConfigurationStructureProvider.getAppConfigurationV2ForAndroid()));
    versionDirectory.addWritableToAll(
        ignoredValue -> Optional.ofNullable(appConfigurationStructureProvider.getAppConfigurationV2ForIos()));
    versionDirectory.addWritableToAll(
        ignoredValue -> Optional.of(diagnosisKeysStructureProvider.getDiagnosisKeys()));
    versionDirectory.addWritableToAll(
        ignoredValue -> Optional.of(statisticsStructureProvider.getStatistics()));

    return new IndexingDecoratorOnDisk<>(versionDirectory, distributionServiceConfig.getOutputFileName());
  }
}
